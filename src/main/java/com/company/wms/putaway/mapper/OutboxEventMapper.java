package com.company.wms.putaway.mapper;

import com.company.wms.putaway.outbox.OutboxEvent;

import com.company.wms.putaway.dto.OutboxEventDto;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventMapper {

    public OutboxEventDto toDto(OutboxEvent entity) {
        if (entity == null) {
            return null;
        }
        return new OutboxEventDto(
                entity.getId(),
                entity.getAggregateType(),
                entity.getAggregateId(),
                entity.getEventType(),
                entity.getTopic(),
                entity.getEventKey(),
                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getAttempts(),
                entity.getLastError(),
                entity.getCreatedAt(),
                entity.getPublishedAt()
        );
    }
}