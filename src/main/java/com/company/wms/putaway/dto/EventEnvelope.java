package com.company.wms.putaway.dto;

import java.time.Instant;

public class EventEnvelope<T> {

    private String eventId;
    private String eventType;
    private String eventVersion;
    private Instant occurredAt;
    private String source;
    private String correlationId;
    private String entityId;
    private T payload;

    public EventEnvelope() {
    }

    public EventEnvelope(String eventId,
                         String eventType,
                         String eventVersion,
                         Instant occurredAt,
                         String source,
                         String correlationId,
                         String entityId,
                         T payload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventVersion = eventVersion;
        this.occurredAt = occurredAt;
        this.source = source;
        this.correlationId = correlationId;
        this.entityId = entityId;
        this.payload = payload;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventVersion() {
        return eventVersion;
    }

    public void setEventVersion(String eventVersion) {
        this.eventVersion = eventVersion;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}