package com.company.wms.putaway.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "processed_event")
public class ProcessedEvent {

	@Id
	@Column(name = "event_id", nullable = false, updatable = false, length = 64)
	private String eventId;

	@Column(name = "event_type", nullable = false, length = 64)
	private String eventType;

	@Column(name = "processed_at", nullable = false)
	private Instant processedAt;

	public ProcessedEvent() {
	}

	public ProcessedEvent(String eventId, String eventType, Instant processedAt) {
		this.eventId = eventId;
		this.eventType = eventType;
		this.processedAt = processedAt;
	}

	public static ProcessedEvent of(String eventId, String eventType) {
		return new ProcessedEvent(eventId, eventType, Instant.now());
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

	public Instant getProcessedAt() {
		return processedAt;
	}

	public void setProcessedAt(Instant processedAt) {
		this.processedAt = processedAt;
	}

	@Override
	public String toString() {
		return "ProcessedEvent{" + "eventId='" + eventId + '\'' + ", eventType='" + eventType + '\'' + ", processedAt="
				+ processedAt + '}';
	}
}