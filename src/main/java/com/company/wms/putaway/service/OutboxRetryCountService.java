
package com.company.wms.putaway.service;

import com.company.wms.putaway.dto.OutboxRetryCountResponse;
import com.company.wms.putaway.event.KafkaEventPublisher;
import com.company.wms.putaway.outbox.OutboxEvent;
import com.company.wms.putaway.outbox.OutboxStatus;
import com.company.wms.putaway.repository.OutboxEventRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class OutboxRetryCountService {

    private final OutboxEventRepository repository;
    private final KafkaEventPublisher kafkaEventPublisher;

    public OutboxRetryCountService(
            OutboxEventRepository repository,
            KafkaEventPublisher kafkaEventPublisher) {
        this.repository = repository;
        this.kafkaEventPublisher = kafkaEventPublisher;
    }

    @Transactional(readOnly = true)
    public Page<OutboxRetryCountResponse> getRetryCounts(
            int page, int size) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be zero or greater");
        }

        if (size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Size must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return repository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public OutboxRetryCountResponse getRetryCount(UUID id) {

        OutboxEvent event = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Outbox event not found: " + id));

        return toResponse(event);
    }

    @Transactional
    public OutboxRetryCountResponse retryFailedEvent(UUID id) {

        // Lock the row so another transaction cannot recover
        // the same FAILED event at the same time.
        OutboxEvent event = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Outbox event not found: " + id));

        if (event.getStatus() != OutboxStatus.FAILED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only FAILED outbox events can be manually retried. "
                            + "Current status: " + event.getStatus());
        }

        try {
            // Publish the original stored event immediately.
            kafkaEventPublisher.publish(
                    event.getTopic(),
                    event.getEventKey(),
                    event.getPayload());

            // Mark PUBLISHED only after the publisher succeeds.
            event.markPublished();

            repository.saveAndFlush(event);

            return toResponse(event);

        } catch (Exception ex) {

            String error = ex.getMessage() != null
                    ? ex.getMessage()
                    : ex.getClass().getSimpleName();

            // Preserve FAILED status and save the recovery error.
            event.recordManualRetryFailure(error);

            repository.saveAndFlush(event);

            // Return the current state so the failure details persist
            // instead of being rolled back by an exception.
            return toResponse(event);
        }
    }

    private OutboxRetryCountResponse toResponse(OutboxEvent event) {

        return new OutboxRetryCountResponse(
                event.getId(),
                event.getEventType(),
                event.getTopic(),
                event.getStatus().name(),
                event.getAttempts() == null ? 0 : event.getAttempts(),
                event.getLastError(),
                event.getCreatedAt(),
                event.getPublishedAt());
    }
}
