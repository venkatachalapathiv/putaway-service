
package com.company.wms.putaway.outbox;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.company.wms.putaway.event.KafkaEventPublisher;
import com.company.wms.putaway.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxPublisher.class);

    private static final String CORRELATION_ID = "correlationId";

    private final OutboxEventRepository repository;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final ObjectMapper objectMapper;

    @Value("${app.outbox.batch-size:100}")
    private int batchSize;

    @Value("${app.outbox.retry.initial-delay-ms:5000}")
    private long initialDelayMs;

    @Value("${app.outbox.retry.max-delay-ms:60000}")
    private long maxDelayMs;

    public OutboxPublisher(
            OutboxEventRepository repository,
            KafkaEventPublisher kafkaEventPublisher,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.kafkaEventPublisher = kafkaEventPublisher;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${app.outbox.poll-delay-ms:5000}")
    @Transactional
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                repository.findReadyForPublishing(
                        OutboxStatus.PENDING,
                        Instant.now(),
                        PageRequest.of(0, batchSize));

        for (OutboxEvent event : events) {

            String previousCorrelationId = MDC.get(CORRELATION_ID);

            try {
                String correlationId =
                        extractCorrelationId(event.getPayload());

                if (correlationId != null && !correlationId.isBlank()) {
                    MDC.put(CORRELATION_ID, correlationId);
                } else {
                    MDC.remove(CORRELATION_ID);
                }

                kafkaEventPublisher.publish(
                        event.getTopic(),
                        event.getEventKey(),
                        event.getPayload());

                event.markPublished();

                log.info(
                        "Outbox event published successfully. "
                                + "eventId={}, topic={}, correlationId={}",
                        event.getId(),
                        event.getTopic(),
                        correlationId);

            } catch (Exception ex) {

                int currentAttempts = event.getAttempts() == null
                        ? 0
                        : event.getAttempts();

                int nextAttempt = currentAttempts + 1;
                long delayMs = calculateRetryDelay(nextAttempt);

                String error = ex.getMessage() != null
                        ? ex.getMessage()
                        : ex.getClass().getSimpleName();

                event.recordFailure(
                        error,
                        Duration.ofMillis(delayMs));

                log.warn(
                        "Outbox publishing failed. eventId={}, "
                                + "attempts={}, retryDelayMs={}, error={}",
                        event.getId(),
                        event.getAttempts(),
                        delayMs,
                        error);

            } finally {
                if (previousCorrelationId == null) {
                    MDC.remove(CORRELATION_ID);
                } else {
                    MDC.put(CORRELATION_ID, previousCorrelationId);
                }
            }
        }
    }

    private String extractCorrelationId(String payload) {
        if (payload == null || payload.isBlank()) {
            return null;
        }

        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode node = root.get(CORRELATION_ID);

            if (node == null || node.isNull()) {
                return null;
            }

            String value = node.asText();
            return value == null || value.isBlank() ? null : value.trim();

        } catch (Exception ex) {
            log.debug("Could not extract correlationId from outbox payload");
            return null;
        }
    }

    private long calculateRetryDelay(int attempt) {
        long delay = initialDelayMs;

        for (int i = 1; i < attempt && delay < maxDelayMs; i++) {
            delay = Math.min(maxDelayMs, delay * 2);
        }

        return delay;
    }
}
