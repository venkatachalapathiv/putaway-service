package com.company.wms.putaway.dto;

import java.time.Instant;

public class PutAwayCompletedPayload {

    private String taskNumber;
    private String goodsReceiptLineId;
    private String skuId;
    private String warehouseId;
    private String targetBinId;
    private Integer quantity;
    private String uom;
    private String completedBy;
    private Instant completedAt;

    public PutAwayCompletedPayload() {
    }

    public PutAwayCompletedPayload(String taskNumber,
                                   String goodsReceiptLineId,
                                   String skuId,
                                   String warehouseId,
                                   String targetBinId,
                                   Integer quantity,
                                   String uom,
                                   String completedBy,
                                   Instant completedAt) {
        this.taskNumber = taskNumber;
        this.goodsReceiptLineId = goodsReceiptLineId;
        this.skuId = skuId;
        this.warehouseId = warehouseId;
        this.targetBinId = targetBinId;
        this.quantity = quantity;
        this.uom = uom;
        this.completedBy = completedBy;
        this.completedAt = completedAt;
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

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getTargetBinId() {
        return targetBinId;
    }

    public void setTargetBinId(String targetBinId) {
        this.targetBinId = targetBinId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}