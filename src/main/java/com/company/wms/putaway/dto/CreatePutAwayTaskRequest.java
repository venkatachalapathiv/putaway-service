package com.company.wms.putaway.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreatePutAwayTaskRequest {

    @NotBlank
    @Size(max = 64)
    private String goodsReceiptLineId;

    @NotBlank
    @Size(max = 64)
    private String skuId;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotBlank
    @Size(max = 64)
    private String targetBinId;
    
    @Size(max = 64)
    private String warehouseId;

    @Size(max = 128)
    private String assignedTo;

    public CreatePutAwayTaskRequest() {
    }

    public CreatePutAwayTaskRequest(String goodsReceiptLineId,
            String skuId,
            Integer quantity,
            String targetBinId,
            String warehouseId,
            String assignedTo) {
this.goodsReceiptLineId = goodsReceiptLineId;
this.skuId = skuId;
this.quantity = quantity;
this.targetBinId = targetBinId;
this.warehouseId = warehouseId;
this.assignedTo = assignedTo;
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

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    @Override
    public String toString() {
        return "CreatePutAwayTaskRequest{" +
                "goodsReceiptLineId='" + goodsReceiptLineId + '\'' +
                ", skuId='" + skuId + '\'' +
                ", quantity=" + quantity +
                ", targetBinId='" + targetBinId + '\'' +
                ", warehouseId='" + warehouseId + '\'' +
                ", assignedTo='" + assignedTo + '\'' +
                '}';
    }
}