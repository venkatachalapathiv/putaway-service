package com.company.wms.putaway.mapper;

import com.company.wms.putaway.domain.PutAwayTask;
import com.company.wms.putaway.dto.PutAwayCompletedPayload;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class EventMapper {

    
     // Entity → Kafka outbound payload.
     
    public PutAwayCompletedPayload toCompletedPayload(PutAwayTask task,
                                                      String warehouseId,
                                                      String uom) {
        if (task == null) {
            return null;
        }
        PutAwayCompletedPayload payload = new PutAwayCompletedPayload();
        payload.setTaskNumber(task.getTaskNumber());
        payload.setGoodsReceiptLineId(task.getGoodsReceiptLineId());
        payload.setSkuId(task.getSkuId());
        payload.setWarehouseId(warehouseId);
        payload.setTargetBinId(task.getTargetBinId());
        payload.setQuantity(task.getQuantity());
        payload.setUom(uom);
        payload.setCompletedBy(task.getAssignedTo());
        payload.setCompletedAt(task.getCompletedAt() != null ? task.getCompletedAt() : Instant.now());
        return payload;
    }
}