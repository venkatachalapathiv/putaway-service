package com.company.wms.putaway.event;

import com.company.wms.putaway.dto.EventEnvelope;

public interface EventPublisher {

    void publish(String topic, String key, EventEnvelope<?> envelope);
}