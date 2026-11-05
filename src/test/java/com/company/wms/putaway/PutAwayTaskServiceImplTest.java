
package com.company.wms.putaway;

import com.company.wms.putaway.domain.PutAwayTask;
import com.company.wms.putaway.domain.PutAwayTaskStatus;
import com.company.wms.putaway.dto.CompletePutAwayRequest;
import com.company.wms.putaway.dto.CreatePutAwayTaskRequest;
import com.company.wms.putaway.dto.EventEnvelope;
import com.company.wms.putaway.dto.PutAwayCompletedPayload;
import com.company.wms.putaway.dto.PutAwayTaskResponse;
import com.company.wms.putaway.event.EventEnvelopeFactory;
import com.company.wms.putaway.event.EventPublisher;
import com.company.wms.putaway.exception.InvalidStateException;
import com.company.wms.putaway.exception.ResourceNotFoundException;
import com.company.wms.putaway.mapper.EventMapper;
import com.company.wms.putaway.mapper.PutAwayTaskMapper;
import com.company.wms.putaway.repository.PutAwayTaskRepository;
import com.company.wms.putaway.service.PutAwayTaskDomainService;
import com.company.wms.putaway.service.PutAwayTaskServiceImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PutAwayTaskServiceImplTest {

    @Mock
    private PutAwayTaskRepository repository;

    @Mock
    private PutAwayTaskMapper taskMapper;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private PutAwayTaskDomainService domainService;

    @Mock
    private EventEnvelopeFactory envelopeFactory;

    @Mock
    private EventPublisher eventPublisher;

    private PutAwayTaskServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PutAwayTaskServiceImpl(
                repository,
                taskMapper,
                eventMapper,
                domainService,
                envelopeFactory,
                eventPublisher
        );
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    // --------------------------------------------------
    // CREATE TASK TESTS
    // --------------------------------------------------

    @Test
    void create_ShouldCreateTaskSuccessfully() {

        CreatePutAwayTaskRequest request =
                new CreatePutAwayTaskRequest(
                        "GRL-1001",
                        "SKU-1007",
                        100,
                        "BIN-007",
                        "WH-001",
                        "operator7"
                );

        PutAwayTask task = new PutAwayTask();
        PutAwayTaskResponse response = new PutAwayTaskResponse();

        MDC.put("correlationId", "test-correlation-id");

        when(repository.existsByGoodsReceiptLineId("GRL-1001"))
                .thenReturn(false);

        when(taskMapper.toEntity(request)).thenReturn(task);
        when(repository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        PutAwayTaskResponse result = service.create(request);

        assertNotNull(result);
        assertNotNull(task.getTaskNumber());
        assertTrue(task.getTaskNumber().startsWith("PUT-"));
        assertEquals(PutAwayTaskStatus.CREATED, task.getStatus());
        assertEquals("test-correlation-id", task.getCorrelationId());

        verify(domainService).validateCreate(100, null);
        verify(repository).existsByGoodsReceiptLineId("GRL-1001");
        verify(repository).save(task);
        verify(taskMapper).toResponse(task);
    }

    @Test
    void create_ShouldRejectDuplicateGoodsReceiptLine() {

        CreatePutAwayTaskRequest request =
                new CreatePutAwayTaskRequest(
                        "GRL-1001",
                        "SKU-1007",
                        100,
                        "BIN-007",
                        "WH-001",
                        "operator7"
                );

        when(repository.existsByGoodsReceiptLineId("GRL-1001"))
                .thenReturn(true);

        assertThrows(
                InvalidStateException.class,
                () -> service.create(request)
        );

        verify(domainService).validateCreate(100, null);
        verify(repository, never()).save(any());
        verify(taskMapper, never()).toEntity(any());
    }

    // --------------------------------------------------
    // GET TASK TESTS
    // --------------------------------------------------

    @Test
    void get_ShouldReturnTaskWhenFound() {

        UUID taskId = UUID.randomUUID();

        PutAwayTask task = new PutAwayTask();
        PutAwayTaskResponse response = new PutAwayTaskResponse();

        when(repository.findById(taskId))
                .thenReturn(Optional.of(task));

        when(taskMapper.toResponse(task))
                .thenReturn(response);

        PutAwayTaskResponse result = service.get(taskId);

        assertNotNull(result);
        assertSame(response, result);

        verify(repository).findById(taskId);
        verify(taskMapper).toResponse(task);
    }

    @Test
    void get_ShouldThrowExceptionWhenTaskNotFound() {

        UUID taskId = UUID.randomUUID();

        when(repository.findById(taskId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.get(taskId)
        );

        verify(taskMapper, never()).toResponse(any());
    }

    // --------------------------------------------------
    // COMPLETE TASK TESTS
    // --------------------------------------------------

    @Test
    void complete_ShouldCompleteTaskAndPublishEvent() {

        UUID taskId = UUID.randomUUID();

        PutAwayTask task = createTask(PutAwayTaskStatus.CREATED);
        task.setTaskNumber("PUT-1001");
        task.setCorrelationId("task-correlation-id");

        CompletePutAwayRequest request =
                new CompletePutAwayRequest(
                        "operator7",
                        "Completed successfully"
                );

        PutAwayTaskResponse response = new PutAwayTaskResponse();

        PutAwayCompletedPayload payload =
                new PutAwayCompletedPayload();

        EventEnvelope<PutAwayCompletedPayload> envelope =
                new EventEnvelope<>();

        MDC.put("correlationId", "previous-correlation-id");

        when(repository.findById(taskId))
                .thenReturn(Optional.of(task));

        when(repository.save(task))
                .thenReturn(task);

        when(eventMapper.toCompletedPayload(
                task,
                task.getWarehouseId(),
                null
        )).thenReturn(payload);

        // FIXED: Explicit generic type avoids EventEnvelope<Object> mismatch.
        when(envelopeFactory.<PutAwayCompletedPayload>of(
                anyString(),
                anyString(),
                any(PutAwayCompletedPayload.class)
        )).thenReturn(envelope);

        when(taskMapper.toResponse(task))
                .thenReturn(response);

        PutAwayTaskResponse result = service.complete(taskId, request);

        assertNotNull(result);
        assertEquals(PutAwayTaskStatus.COMPLETED, task.getStatus());
        assertEquals("operator7", task.getAssignedTo());

        verify(repository).save(task);

        verify(eventMapper).toCompletedPayload(
                task,
                task.getWarehouseId(),
                null
        );

        verify(envelopeFactory).of(
                anyString(),
                eq("PUT-1001"),
                eq(payload)
        );

        verify(eventPublisher).publish(
                anyString(),
                eq("PUT-1001"),
                eq(envelope)
        );

        assertEquals(
                "previous-correlation-id",
                MDC.get("correlationId")
        );
    }

    @Test
    void complete_ShouldThrowExceptionWhenTaskNotFound() {

        UUID taskId = UUID.randomUUID();

        when(repository.findById(taskId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.complete(
                        taskId,
                        new CompletePutAwayRequest("operator7", null)
                )
        );

        verify(repository, never()).save(any());

        verify(eventPublisher, never())
                .publish(anyString(), anyString(), any());
    }

    @Test
    void complete_ShouldRejectAlreadyCompletedTask() {

        UUID taskId = UUID.randomUUID();

        PutAwayTask task = createTask(PutAwayTaskStatus.COMPLETED);

        when(repository.findById(taskId))
                .thenReturn(Optional.of(task));

        assertThrows(
                InvalidStateException.class,
                () -> service.complete(
                        taskId,
                        new CompletePutAwayRequest("operator7", null)
                )
        );

        verify(repository, never()).save(any());

        verify(eventPublisher, never())
                .publish(anyString(), anyString(), any());
    }

    @Test
    void complete_ShouldRejectCancelledTask() {

        UUID taskId = UUID.randomUUID();

        PutAwayTask task = createTask(PutAwayTaskStatus.CANCELLED);

        when(repository.findById(taskId))
                .thenReturn(Optional.of(task));

        assertThrows(
                InvalidStateException.class,
                () -> service.complete(
                        taskId,
                        new CompletePutAwayRequest("operator7", null)
                )
        );

        verify(repository, never()).save(any());

        verify(eventPublisher, never())
                .publish(anyString(), anyString(), any());
    }

    @Test
    void complete_ShouldRestoreMdcWhenPublishingFails() {

        UUID taskId = UUID.randomUUID();

        PutAwayTask task = createTask(PutAwayTaskStatus.CREATED);
        task.setTaskNumber("PUT-1002");
        task.setCorrelationId("task-correlation-id");

        PutAwayCompletedPayload payload =
                new PutAwayCompletedPayload();

        EventEnvelope<PutAwayCompletedPayload> envelope =
                new EventEnvelope<>();

        MDC.put("correlationId", "previous-correlation-id");

        when(repository.findById(taskId))
                .thenReturn(Optional.of(task));

        when(repository.save(task))
                .thenReturn(task);

        when(eventMapper.toCompletedPayload(
                task,
                task.getWarehouseId(),
                null
        )).thenReturn(payload);

        // FIXED: Explicit generic type avoids EventEnvelope<Object> mismatch.
        when(envelopeFactory.<PutAwayCompletedPayload>of(
                anyString(),
                anyString(),
                any(PutAwayCompletedPayload.class)
        )).thenReturn(envelope);

        doThrow(new RuntimeException("Kafka publishing failed"))
                .when(eventPublisher)
                .publish(anyString(), anyString(), any());

        assertThrows(
                RuntimeException.class,
                () -> service.complete(
                        taskId,
                        new CompletePutAwayRequest("operator7", null)
                )
        );

        assertEquals(
                "previous-correlation-id",
                MDC.get("correlationId")
        );
    }

    // --------------------------------------------------
    // TEST DATA
    // --------------------------------------------------

    private PutAwayTask createTask(PutAwayTaskStatus status) {

        PutAwayTask task = new PutAwayTask();

        task.setId(UUID.randomUUID());
        task.setTaskNumber("PUT-TEST-001");
        task.setGoodsReceiptLineId("GRL-1001");
        task.setSkuId("SKU-1007");
        task.setQuantity(100);
        task.setTargetBinId("BIN-007");
        task.setWarehouseId("WH-001");
        task.setAssignedTo("operator7");
        task.setStatus(status);

        return task;
    }
}
