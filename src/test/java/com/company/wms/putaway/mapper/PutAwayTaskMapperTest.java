
package com.company.wms.putaway.mapper;

import com.company.wms.putaway.domain.PutAwayTask;
import com.company.wms.putaway.domain.PutAwayTaskStatus;
import com.company.wms.putaway.dto.CreatePutAwayTaskRequest;
import com.company.wms.putaway.dto.PutAwayTaskResponse;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PutAwayTaskMapperTest {

    private final PutAwayTaskMapper mapper = new PutAwayTaskMapper();

    @Test
    void toEntity_ShouldMapAllRequestFields() {

        CreatePutAwayTaskRequest request =
                new CreatePutAwayTaskRequest(
                        "GRL-1001",
                        "SKU-1007",
                        100,
                        "BIN-007",
                        "WH-001",
                        "operator7"
                );

        PutAwayTask entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals("GRL-1001", entity.getGoodsReceiptLineId());
        assertEquals("SKU-1007", entity.getSkuId());
        assertEquals(100, entity.getQuantity());
        assertEquals("BIN-007", entity.getTargetBinId());
        assertEquals("WH-001", entity.getWarehouseId());
        assertEquals("operator7", entity.getAssignedTo());
        assertEquals(PutAwayTaskStatus.CREATED, entity.getStatus());
        assertNull(entity.getId());
        assertNull(entity.getTaskNumber());
    }

    @Test
    void toEntity_ShouldReturnNull_WhenRequestIsNull() {

        PutAwayTask entity = mapper.toEntity(null);

        assertNull(entity);
    }

    @Test
    void toEntity_ShouldPreserveNullOptionalFields() {

        CreatePutAwayTaskRequest request =
                new CreatePutAwayTaskRequest(
                        "GRL-1002",
                        "SKU-1008",
                        50,
                        "BIN-008",
                        null,
                        null
                );

        PutAwayTask entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertNull(entity.getWarehouseId());
        assertNull(entity.getAssignedTo());
        assertEquals(PutAwayTaskStatus.CREATED, entity.getStatus());
    }

    @Test
    void toResponse_ShouldMapAllEntityFields() {

        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Instant completedAt = Instant.now();

        PutAwayTask entity = new PutAwayTask();
        entity.setId(id);
        entity.setTaskNumber("PUT-1001");
        entity.setGoodsReceiptLineId("GRL-1001");
        entity.setSkuId("SKU-1007");
        entity.setQuantity(100);
        entity.setTargetBinId("BIN-007");
        entity.setWarehouseId("WH-001");
        entity.setStatus(PutAwayTaskStatus.COMPLETED);
        entity.setAssignedTo("operator7");
        entity.setCreatedAt(createdAt);
        entity.setCompletedAt(completedAt);

        PutAwayTaskResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("PUT-1001", response.getTaskNumber());
        assertEquals("GRL-1001", response.getGoodsReceiptLineId());
        assertEquals("SKU-1007", response.getSkuId());
        assertEquals(100, response.getQuantity());
        assertEquals("BIN-007", response.getTargetBinId());
        assertEquals("COMPLETED", response.getStatus());
        assertEquals("operator7", response.getAssignedTo());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(completedAt, response.getCompletedAt());
    }

    @Test
    void toResponse_ShouldReturnNull_WhenEntityIsNull() {

        PutAwayTaskResponse response = mapper.toResponse(null);

        assertNull(response);
    }

    @Test
    void toResponse_ShouldMapNullStatusAsNull() {

        PutAwayTask entity = new PutAwayTask();
        entity.setStatus(null);

        PutAwayTaskResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertNull(response.getStatus());
    }

    @Test
    void toResponse_ShouldMapCreatedStatusCorrectly() {

        PutAwayTask entity = new PutAwayTask();
        entity.setStatus(PutAwayTaskStatus.CREATED);

        PutAwayTaskResponse response = mapper.toResponse(entity);

        assertEquals("CREATED", response.getStatus());
    }
}
