package com.company.wms.putaway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.company.wms.putaway.outbox.OutboxEvent;
import com.company.wms.putaway.repository.OutboxEventRepository;

import java.util.UUID;

@Service
public class OutboxEventServiceImpl implements OutboxEventService {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventServiceImpl.class);

    private final OutboxEventRepository repository;

    public OutboxEventServiceImpl(OutboxEventRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public UUID enqueue(String aggregateType,
                        String aggregateId,
                        String eventType,
                        String topic,
                        String eventKey,
                        String payloadJson) {

        OutboxEvent event = OutboxEvent.pending(
                aggregateType, aggregateId, eventType, topic, eventKey, payloadJson);

        OutboxEvent saved = repository.save(event);
        log.debug("Outbox event enqueued id={} type={} aggregate={}/{}",
                saved.getId(), eventType, aggregateType, aggregateId);
        return saved.getId();
    }
}