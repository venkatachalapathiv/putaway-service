
package com.company.wms.putaway.service;

import com.company.wms.putaway.domain.ProcessedEvent;
import com.company.wms.putaway.dto.CreatePutAwayTaskRequest;
import com.company.wms.putaway.dto.EventEnvelope;
import com.company.wms.putaway.dto.GoodsReceivedLine;
import com.company.wms.putaway.dto.GoodsReceivedPayload;
import com.company.wms.putaway.event.KafkaTopics;
import com.company.wms.putaway.exception.BusinessRuleException;
import com.company.wms.putaway.repository.ProcessedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class GoodsReceivedConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(GoodsReceivedConsumer.class);

    private static final String CORRELATION_ID = "correlationId";

    private final ProcessedEventRepository processedEventRepository;
    private final PutAwayTaskService putAwayTaskService;
    private final ObjectMapper objectMapper;

    public GoodsReceivedConsumer(
            ProcessedEventRepository processedEventRepository,
            PutAwayTaskService putAwayTaskService,
            ObjectMapper objectMapper) {
        this.processedEventRepository = processedEventRepository;
        this.putAwayTaskService = putAwayTaskService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = KafkaTopics.GOODS_RECEIVED,
            groupId = "${spring.kafka.consumer.group-id:putaway-service}")
    @Transactional
    public void onGoodsReceived(String rawMessage) {

        EventEnvelope<GoodsReceivedPayload> envelope =
                deserialize(rawMessage);

        if (envelope == null) {
            return;
        }

        // Preserve any MDC value that existed before this message.
        String previousCorrelationId = MDC.get(CORRELATION_ID);

        try {
            String correlationId = envelope.getCorrelationId();

            // Fallback only for older events that do not contain a correlation ID.
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
                log.warn(
                        "GoodsReceived event {} has no correlationId; "
                                + "generated fallback correlationId={}",
                        envelope.getEventId(),
                        correlationId);
            }

            // Use the Inbound correlation ID for all logs during processing.
            MDC.put(CORRELATION_ID, correlationId);

            String eventId = envelope.getEventId();

            log.info(
                    "Received GoodsReceived event: eventId={}, correlationId={}",
                    eventId,
                    correlationId);

            if (eventId == null || eventId.isBlank()) {
                log.warn("GoodsReceived event has no eventId; ignoring message");
                return;
            }

            if (processedEventRepository.existsByEventId(eventId)) {
                log.info("Duplicate event {} ignored", eventId);
                return;
            }

            GoodsReceivedPayload payload = envelope.getPayload();

            if (payload == null
                    || payload.getLines() == null
                    || payload.getLines().isEmpty()) {
                log.warn("GoodsReceived event {} has no lines", eventId);

                processedEventRepository.save(
                        ProcessedEvent.of(eventId, envelope.getEventType()));
                return;
            }

            for (GoodsReceivedLine line : payload.getLines()) {
                try {
                    CreatePutAwayTaskRequest request =
                            new CreatePutAwayTaskRequest(
                                    line.getGoodsReceiptLineId(),
                                    line.getSkuId(),
                                    line.getReceivedQuantity(),
                                    line.getTargetBinId(),
                                    payload.getWarehouseId(),
                                    null);

                    putAwayTaskService.create(request);

                    log.info(
                            "Created put-away task for receipt line {}",
                            line.getGoodsReceiptLineId());

                } catch (BusinessRuleException | IllegalStateException ex) {
                    log.warn(
                            "Skipped receipt line {}: {}",
                            line.getGoodsReceiptLineId(),
                            ex.getMessage());
                }
            }

            processedEventRepository.save(
                    ProcessedEvent.of(eventId, envelope.getEventType()));

            log.info(
                    "Finished processing GoodsReceived event: eventId={}, correlationId={}",
                    eventId,
                    correlationId);

        } finally {
            // Prevent one Kafka message's ID leaking into another message.
            if (previousCorrelationId == null) {
                MDC.remove(CORRELATION_ID);
            } else {
                MDC.put(CORRELATION_ID, previousCorrelationId);
            }
        }
    }

    private EventEnvelope<GoodsReceivedPayload> deserialize(String rawMessage) {
        try {
            return objectMapper.readValue(
                    rawMessage,
                    objectMapper.getTypeFactory().constructParametricType(
                            EventEnvelope.class,
                            GoodsReceivedPayload.class));
        } catch (Exception ex) {
            log.error("Failed to deserialize GoodsReceived event", ex);
            throw new IllegalArgumentException(
                    "Invalid GoodsReceived event JSON", ex);
        }
    }
}
