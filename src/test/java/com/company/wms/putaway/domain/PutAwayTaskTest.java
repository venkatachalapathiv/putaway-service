
package com.company.wms.putaway.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PutAwayTaskTest {

    private PutAwayTask createTask(PutAwayTaskStatus status) {
        PutAwayTask task = new PutAwayTask();
        task.setTaskNumber("PUT-1001");
        task.setGoodsReceiptLineId("GRL-1001");
        task.setSkuId("SKU-1007");
        task.setQuantity(100);
        task.setTargetBinId("BIN-007");
        task.setWarehouseId("WH-001");
        task.setStatus(status);
        return task;
    }

    @Test
    void assignTo_ShouldAssignTaskSuccessfully() {

        PutAwayTask task = createTask(PutAwayTaskStatus.CREATED);

        task.assignTo("operator7");

        assertEquals(PutAwayTaskStatus.ASSIGNED, task.getStatus());
        assertEquals("operator7", task.getAssignedTo());
    }

    @Test
    void start_ShouldStartCreatedTask() {

        PutAwayTask task = createTask(PutAwayTaskStatus.CREATED);

        task.start();

        assertEquals(PutAwayTaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void start_ShouldStartAssignedTask() {

        PutAwayTask task = createTask(PutAwayTaskStatus.ASSIGNED);

        task.start();

        assertEquals(PutAwayTaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void complete_ShouldCompleteTaskSuccessfully() {

        PutAwayTask task = createTask(PutAwayTaskStatus.IN_PROGRESS);

        task.complete();

        assertEquals(PutAwayTaskStatus.COMPLETED, task.getStatus());
        assertNotNull(task.getCompletedAt());
    }

    @Test
    void complete_ShouldSetCompletedTimestamp() {

        PutAwayTask task = createTask(PutAwayTaskStatus.CREATED);

        Instant beforeCompletion = Instant.now();

        task.complete();

        Instant afterCompletion = Instant.now();

        assertNotNull(task.getCompletedAt());
        assertFalse(task.getCompletedAt().isBefore(beforeCompletion));
        assertFalse(task.getCompletedAt().isAfter(afterCompletion));
    }

    @Test
    void cancel_ShouldCancelTaskSuccessfully() {

        PutAwayTask task = createTask(PutAwayTaskStatus.CREATED);

        task.cancel();

        assertEquals(PutAwayTaskStatus.CANCELLED, task.getStatus());
    }

    @Test
    void assignTo_ShouldRejectCompletedTask() {

        PutAwayTask task = createTask(PutAwayTaskStatus.COMPLETED);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> task.assignTo("operator7")
        );

        assertEquals("Cannot assign a completed task", exception.getMessage());
    }

    @Test
    void assignTo_ShouldRejectCancelledTask() {

        PutAwayTask task = createTask(PutAwayTaskStatus.CANCELLED);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> task.assignTo("operator7")
        );

        assertEquals("Cannot assign a cancelled task", exception.getMessage());
    }

    @Test
    void start_ShouldRejectCompletedTask() {

        PutAwayTask task = createTask(PutAwayTaskStatus.COMPLETED);

        assertThrows(
                IllegalStateException.class,
                task::start
        );
    }

    @Test
    void start_ShouldRejectCancelledTask() {

        PutAwayTask task = createTask(PutAwayTaskStatus.CANCELLED);

        assertThrows(
                IllegalStateException.class,
                task::start
        );
    }

    @Test
    void complete_ShouldRejectAlreadyCompletedTask() {

        PutAwayTask task = createTask(PutAwayTaskStatus.COMPLETED);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                task::complete
        );

        assertEquals("Task already completed", exception.getMessage());
    }

    @Test
    void complete_ShouldRejectCancelledTask() {

        PutAwayTask task = createTask(PutAwayTaskStatus.CANCELLED);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                task::complete
        );

        assertEquals(
                "Cancelled task cannot be completed",
                exception.getMessage()
        );
    }

    @Test
    void cancel_ShouldRejectCompletedTask() {

        PutAwayTask task = createTask(PutAwayTaskStatus.COMPLETED);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                task::cancel
        );

        assertEquals(
                "Completed task cannot be cancelled",
                exception.getMessage()
        );
    }

    @Test
    void cancel_ShouldAllowCancellingAssignedTask() {

        PutAwayTask task = createTask(PutAwayTaskStatus.ASSIGNED);

        task.cancel();

        assertEquals(PutAwayTaskStatus.CANCELLED, task.getStatus());
    }
}
