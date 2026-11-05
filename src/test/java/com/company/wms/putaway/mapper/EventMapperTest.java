
package com.company.wms.putaway.mapper;

import com.company.wms.putaway.domain.PutAwayTask;
import com.company.wms.putaway.dto.PutAwayCompletedPayload;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class EventMapperTest {

    private final EventMapper mapper = new EventMapper();

    @Test
    void toCompletedPayload_ShouldMapAllFields() {

        Instant completedAt = Instant.parse("2026-11-04T10:30:00Z");

        PutAwayTask task = new PutAwayTask();
        task.setTaskNumber("PUT-1001");
        task.setGoodsReceiptLineId("GRL-1001");
        task.setSkuId("SKU-1007");
        task.setQuantity(100);
        task.setTargetBinId("BIN-007");
        task.setAssignedTo("operator7");
        task.setCompletedAt(completedAt);

        PutAwayCompletedPayload payload =
                mapper.toCompletedPayload(task, "WH-001", "PCS");

        assertNotNull(payload);
        assertEquals("PUT-1001", payload.getTaskNumber());
        assertEquals("GRL-1001", payload.getGoodsReceiptLineId());
        assertEquals("SKU-1007", payload.getSkuId());
        assertEquals("WH-001", payload.getWarehouseId());
        assertEquals("BIN-007", payload.getTargetBinId());
        assertEquals(100, payload.getQuantity());
        assertEquals("PCS", payload.getUom());
        assertEquals("operator7", payload.getCompletedBy());
        assertEquals(completedAt, payload.getCompletedAt());
    }

    @Test
    void toCompletedPayload_ShouldReturnNull_WhenTaskIsNull() {

        PutAwayCompletedPayload payload =
                mapper.toCompletedPayload(null, "WH-001", "PCS");

        assertNull(payload);
    }

    @Test
    void toCompletedPayload_ShouldUseCurrentTime_WhenCompletedAtIsNull() {

        PutAwayTask task = new PutAwayTask();
        task.setTaskNumber("PUT-1002");
        task.setAssignedTo("operator8");

        Instant beforeMapping = Instant.now();

        PutAwayCompletedPayload payload =
                mapper.toCompletedPayload(task, "WH-002", "BOX");

        Instant afterMapping = Instant.now();

        assertNotNull(payload);
        assertNotNull(payload.getCompletedAt());

        assertFalse(payload.getCompletedAt().isBefore(beforeMapping));
        assertFalse(payload.getCompletedAt().isAfter(afterMapping));
    }

    @Test
    void toCompletedPayload_ShouldPreserveNullOptionalFields() {

        PutAwayTask task = new PutAwayTask();
        task.setTaskNumber("PUT-1003");

        PutAwayCompletedPayload payload =
                mapper.toCompletedPayload(task, null, null);

        assertNotNull(payload);
        assertEquals("PUT-1003", payload.getTaskNumber());
        assertNull(payload.getWarehouseId());
        assertNull(payload.getUom());
        assertNull(payload.getCompletedBy());
    }

    @Test
    void toCompletedPayload_ShouldMapQuantityCorrectly() {

        PutAwayTask task = new PutAwayTask();
        task.setQuantity(250);

        PutAwayCompletedPayload payload =
                mapper.toCompletedPayload(task, "WH-001", "PCS");

        assertEquals(250, payload.getQuantity());
    }
}
