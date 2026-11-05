
package com.company.wms.putaway.service;

import com.company.wms.putaway.domain.PutAwayTask;
import com.company.wms.putaway.domain.PutAwayTaskStatus;
import com.company.wms.putaway.dto.CompletePutAwayRequest;
import com.company.wms.putaway.dto.CreatePutAwayTaskRequest;
import com.company.wms.putaway.dto.EventEnvelope;
import com.company.wms.putaway.dto.PutAwayCompletedPayload;
import com.company.wms.putaway.dto.PutAwayTaskResponse;
import com.company.wms.putaway.event.EventEnvelopeFactory;
import com.company.wms.putaway.event.EventPublisher;
import com.company.wms.putaway.event.KafkaTopics;
import com.company.wms.putaway.event.PutAwayEventType;
import com.company.wms.putaway.exception.InvalidStateException;
import com.company.wms.putaway.exception.ResourceNotFoundException;
import com.company.wms.putaway.mapper.EventMapper;
import com.company.wms.putaway.mapper.PutAwayTaskMapper;
import com.company.wms.putaway.repository.PutAwayTaskRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PutAwayTaskServiceImpl implements PutAwayTaskService {

    private static final Logger log =
            LoggerFactory.getLogger(PutAwayTaskServiceImpl.class);

    private static final String CORRELATION_ID = "correlationId";

    private final PutAwayTaskRepository repository;
    private final PutAwayTaskMapper taskMapper;
    private final EventMapper eventMapper;
    private final PutAwayTaskDomainService domainService;
    private final EventEnvelopeFactory envelopeFactory;
    private final EventPublisher eventPublisher;

    public PutAwayTaskServiceImpl(
            PutAwayTaskRepository repository,
            PutAwayTaskMapper taskMapper,
            EventMapper eventMapper,
            PutAwayTaskDomainService domainService,
            EventEnvelopeFactory envelopeFactory,
            EventPublisher eventPublisher) {

        this.repository = repository;
        this.taskMapper = taskMapper;
        this.eventMapper = eventMapper;
        this.domainService = domainService;
        this.envelopeFactory = envelopeFactory;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public PutAwayTaskResponse create(CreatePutAwayTaskRequest request) {

        domainService.validateCreate(request.getQuantity(), null);

        if (repository.existsByGoodsReceiptLineId(
                request.getGoodsReceiptLineId())) {
            throw new InvalidStateException(
                    "A put-away task already exists for this receipt line");
        }

        PutAwayTask task = taskMapper.toEntity(request);
        task.setTaskNumber(generateTaskNumber());
        task.setStatus(PutAwayTaskStatus.CREATED);

        // Preserve the correlation ID received from the inbound event.
        String correlationId = MDC.get(CORRELATION_ID);

        if (correlationId != null && !correlationId.isBlank()) {
            task.setCorrelationId(correlationId);
        }

        PutAwayTask saved = repository.save(task);

        log.info(
                "Created put-away task: taskNumber={}, correlationId={}",
                saved.getTaskNumber(),
                saved.getCorrelationId());

        return taskMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PutAwayTaskResponse get(UUID id) {

        PutAwayTask task = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found: " + id));

        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public PutAwayTaskResponse complete(
            UUID id,
            CompletePutAwayRequest request) {

        PutAwayTask task = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found: " + id));

        // Save the existing thread context before changing it.
        String previousCorrelationId = MDC.get(CORRELATION_ID);

        try {
            // Restore the correlation ID saved with this task.
            String taskCorrelationId = task.getCorrelationId();

            if (taskCorrelationId != null && !taskCorrelationId.isBlank()) {
                MDC.put(CORRELATION_ID, taskCorrelationId);
            } else if (previousCorrelationId == null
                    || previousCorrelationId.isBlank()) {
                MDC.remove(CORRELATION_ID);

                log.warn(
                        "No correlationId found for put-away task: taskNumber={}",
                        task.getTaskNumber());
            }

            String correlationId = MDC.get(CORRELATION_ID);

            log.info(
                    "Starting put-away completion: taskNumber={}, taskId={}, correlationId={}",
                    task.getTaskNumber(),
                    task.getId(),
                    correlationId);

            try {
                task.complete();
            } catch (IllegalStateException ex) {
                throw new InvalidStateException(ex.getMessage());
            }

            if (request != null && request.getCompletedBy() != null) {
                task.setAssignedTo(request.getCompletedBy());
            }

            PutAwayTask saved = repository.save(task);

            PutAwayCompletedPayload payload =
                    eventMapper.toCompletedPayload(
                            saved,
                            saved.getWarehouseId(),
                            null);

            // The envelope factory reads the correlation ID from MDC.
            EventEnvelope<PutAwayCompletedPayload> envelope =
                    envelopeFactory.of(
                            PutAwayEventType.PUTAWAY_COMPLETED.value(),
                            saved.getTaskNumber(),
                            payload);

            log.info(
                    "Preparing PutAwayCompleted event: taskNumber={}, eventId={}, correlationId={}",
                    saved.getTaskNumber(),
                    envelope.getEventId(),
                    envelope.getCorrelationId());

            eventPublisher.publish(
                    KafkaTopics.PUTAWAY_COMPLETED,
                    saved.getTaskNumber(),
                    envelope);

            log.info(
                    "PutAwayCompleted event submitted: taskNumber={}, topic={}, warehouseId={}, skuId={}, quantity={}, correlationId={}",
                    saved.getTaskNumber(),
                    KafkaTopics.PUTAWAY_COMPLETED,
                    saved.getWarehouseId(),
                    saved.getSkuId(),
                    saved.getQuantity(),
                    envelope.getCorrelationId());

            return taskMapper.toResponse(saved);

        } catch (RuntimeException ex) {
            log.error(
                    "Put-away completion failed: taskNumber={}, correlationId={}, error={}",
                    task.getTaskNumber(),
                    MDC.get(CORRELATION_ID),
                    ex.getMessage(),
                    ex);
            throw ex;

        } finally {
            // Prevent MDC values from leaking into reused threads.
            if (previousCorrelationId == null) {
                MDC.remove(CORRELATION_ID);
            } else {
                MDC.put(CORRELATION_ID, previousCorrelationId);
            }
        }
    }

    private String generateTaskNumber() {
        return "PUT-" + System.currentTimeMillis();
    }
}
