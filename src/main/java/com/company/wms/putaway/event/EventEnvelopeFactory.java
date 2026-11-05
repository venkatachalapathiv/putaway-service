
package com.company.wms.putaway.event;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.company.wms.putaway.dto.EventEnvelope;

@Component
public class EventEnvelopeFactory {

    private static final Logger log =
            LoggerFactory.getLogger(EventEnvelopeFactory.class);

    private static final String CORRELATION_MDC_KEY = "correlationId";
    private static final String DEFAULT_EVENT_VERSION = "1.0";

    private final String source;

    public EventEnvelopeFactory(
            @Value("${spring.application.name:putaway-service}")
            String source) {
        this.source = source;
    }

    public <T> EventEnvelope<T> of(
            String eventType,
            String entityId,
            T payload) {

        return of(
                eventType,
                DEFAULT_EVENT_VERSION,
                entityId,
                payload
        );
    }

    public <T> EventEnvelope<T> of(
            String eventType,
            String eventVersion,
            String entityId,
            T payload) {

        String eventId = UUID.randomUUID().toString();
        Instant occurredAt = Instant.now();
        String correlationId = currentCorrelationId();

        EventEnvelope<T> envelope = new EventEnvelope<>();

        envelope.setEventId(eventId);
        envelope.setEventType(eventType);
        envelope.setEventVersion(
                eventVersion != null
                        ? eventVersion
                        : DEFAULT_EVENT_VERSION
        );
        envelope.setOccurredAt(occurredAt);
        envelope.setSource(source);
        envelope.setCorrelationId(correlationId);
        envelope.setEntityId(entityId);
        envelope.setPayload(payload);

        log.debug(
                "Built event envelope. eventId={}, type={}, "
                        + "entityId={}, correlationId={}",
                eventId,
                eventType,
                entityId,
                correlationId
        );

        return envelope;
    }

    private String currentCorrelationId() {
        String correlationId = MDC.get(CORRELATION_MDC_KEY);

        if (correlationId != null && !correlationId.isBlank()) {
            correlationId = correlationId.trim();

            log.debug(
                    "Correlation ID found in MDC: correlationId={}",
                    correlationId
            );

            return correlationId;
        }

        String generatedId = UUID.randomUUID().toString();

        log.warn(
                "No correlation ID found in MDC while creating event; "
                        + "generated fallback ID: correlationId={}",
                generatedId
        );

        return generatedId;
    }
}
