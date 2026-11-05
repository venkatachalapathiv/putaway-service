package com.company.wms.putaway.service;

import java.util.UUID;

public interface OutboxEventService {

	UUID enqueue(String aggregateType, String aggregateId, String eventType, String topic, String eventKey,
			String payloadJson);
}