
package com.company.wms.putaway.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 64)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 64)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, length = 64)
    private String eventType;

    @Column(name = "topic", nullable = false, length = 128)
    private String topic;

    @Column(name = "event_key", length = 128)
    private String eventKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private OutboxStatus status;

    @Column(name = "attempts", nullable = false)
    private Integer attempts;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    // New field added by V4 migration.
    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    public OutboxEvent() {
    }

    public OutboxEvent(
            UUID id,
            String aggregateType,
            String aggregateId,
            String eventType,
            String topic,
            String eventKey,
            String payload,
            OutboxStatus status,
            Integer attempts,
            String lastError,
            Instant createdAt,
            Instant publishedAt) {

        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.topic = topic;
        this.eventKey = eventKey;
        this.payload = payload;
        this.status = status;
        this.attempts = attempts;
        this.lastError = lastError;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
    }

    public static OutboxEvent pending(
            String aggregateType,
            String aggregateId,
            String eventType,
            String topic,
            String eventKey,
            String payloadJson) {

        OutboxEvent event = new OutboxEvent();
        event.aggregateType = aggregateType;
        event.aggregateId = aggregateId;
        event.eventType = eventType;
        event.topic = topic;
        event.eventKey = eventKey;
        event.payload = payloadJson;
        event.status = OutboxStatus.PENDING;
        event.attempts = 0;
        event.createdAt = Instant.now();
        event.nextAttemptAt = event.createdAt;

        return event;
    }

    public void markPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = Instant.now();
        this.lastError = null;
        this.nextAttemptAt = null;
    }

    /**
     * Existing failure method retained for compatibility
     * with existing manual retry or other code.
     */
    public void recordFailure(String error, int maxAttempts) {
        this.attempts = (this.attempts == null ? 0 : this.attempts) + 1;

        this.lastError = truncateError(error);

        if (this.attempts >= maxAttempts) {
            this.status = OutboxStatus.FAILED;
        }
    }

    /**
     * Records a transient publishing failure and schedules
     * another automatic attempt without permanently failing
     * the event.
     */
    public void recordFailure(String error, Duration retryDelay) {
        this.attempts = (this.attempts == null ? 0 : this.attempts) + 1;

        this.lastError = truncateError(error);
        this.status = OutboxStatus.PENDING;
        this.nextAttemptAt = Instant.now().plus(retryDelay);
    }
    public void recordManualRetryFailure(String error) {
        this.attempts = (this.attempts == null ? 0 : this.attempts) + 1;
        this.lastError = truncateError(error);
        this.status = OutboxStatus.FAILED;
        this.nextAttemptAt = null;
    }

    private String truncateError(String error) {
        String message = (error == null || error.isBlank())
                ? "Unknown Kafka publishing error"
                : error;

        return message.length() > 1000
                ? message.substring(0, 1000)
                : message;
    }

    public void requeueForManualRetry() {
        if (this.status != OutboxStatus.FAILED) {
            throw new IllegalStateException(
                    "Only FAILED outbox events can be manually retried");
        }

        // Preserve the existing attempt count and error for tracking.
        this.status = OutboxStatus.PENDING;
        this.nextAttemptAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }

    public void setNextAttemptAt(Instant nextAttemptAt) {
        this.nextAttemptAt = nextAttemptAt;
    }

    @Override
    public String toString() {
        return "OutboxEvent{" +
                "id=" + id +
                ", aggregateType='" + aggregateType + '\'' +
                ", aggregateId='" + aggregateId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", topic='" + topic + '\'' +
                ", eventKey='" + eventKey + '\'' +
                ", status=" + status +
                ", attempts=" + attempts +
                ", lastError='" + lastError + '\'' +
                ", createdAt=" + createdAt +
                ", publishedAt=" + publishedAt +
                ", nextAttemptAt=" + nextAttemptAt +
                '}';
    }
}
