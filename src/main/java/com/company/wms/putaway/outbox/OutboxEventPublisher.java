
package com.company.wms.putaway.outbox;

import com.company.wms.putaway.dto.EventEnvelope;
import com.company.wms.putaway.event.EventPublisher;
import com.company.wms.putaway.service.OutboxEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventPublisher implements EventPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxEventPublisher.class);

    private final OutboxEventService outboxEventService;
    private final ObjectMapper objectMapper;

    public OutboxEventPublisher(
            OutboxEventService outboxEventService,
            ObjectMapper objectMapper) {

        this.outboxEventService = outboxEventService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(
            String topic,
            String key,
            EventEnvelope<?> envelope) {

        final String payloadJson;

        try {
            payloadJson = objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                    "Failed to serialize event for topic " + topic,
                    ex
            );
        }

        String entityId = envelope.getEntityId() != null
                ? envelope.getEntityId()
                : key;

        outboxEventService.enqueue(
                "PutAwayTask",
                entityId,
                envelope.getEventType(),
                topic,
                key,
                payloadJson
        );

        log.info(
                "Putaway event saved to outbox: type={}, topic={}, key={}, eventId={}, correlationId={}",
                envelope.getEventType(),
                topic,
                key,
                envelope.getEventId(),
                envelope.getCorrelationId()
        );
        
    }
}
