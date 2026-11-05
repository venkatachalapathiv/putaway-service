package com.company.wms.putaway.dto;

import java.time.Instant;
import java.util.UUID;

public class PutAwayTaskResponse {

    private UUID id;
    private String taskNumber;
    private String goodsReceiptLineId;
    private String skuId;
    private Integer quantity;
    private String targetBinId;
    private String status;
    private String assignedTo;
    private Instant createdAt;
    private Instant completedAt;

    public PutAwayTaskResponse() {
    }

    public PutAwayTaskResponse(UUID id,
                               String taskNumber,
                               String goodsReceiptLineId,
                               String skuId,
                               Integer quantity,
                               String targetBinId,
                               String status,
                               String assignedTo,
                               Instant createdAt,
                               Instant completedAt) {
        this.id = id;
        this.taskNumber = taskNumber;
        this.goodsReceiptLineId = goodsReceiptLineId;
        this.skuId = skuId;
        this.quantity = quantity;
        this.targetBinId = targetBinId;
        this.status = status;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    public String getGoodsReceiptLineId() {
        return goodsReceiptLineId;
    }

    public void setGoodsReceiptLineId(String goodsReceiptLineId) {
        this.goodsReceiptLineId = goodsReceiptLineId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getTargetBinId() {
        return targetBinId;
    }

    public void setTargetBinId(String targetBinId) {
        this.targetBinId = targetBinId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}