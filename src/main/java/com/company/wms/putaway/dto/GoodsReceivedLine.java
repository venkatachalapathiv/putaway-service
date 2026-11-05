package com.company.wms.putaway.dto;

public class GoodsReceivedLine {

    private String goodsReceiptLineId;
    private String skuId;
    private Integer expectedQuantity;
    private Integer receivedQuantity;
    private String uom;
    private String batchNumber;
    private String serialNumber;
    private String targetBinId;  

    public GoodsReceivedLine() {
    }

    public GoodsReceivedLine(String goodsReceiptLineId,
                             String skuId,
                             Integer expectedQuantity,
                             Integer receivedQuantity,
                             String uom,
                             String batchNumber,
                             String serialNumber,
                             String targetBinId) {
        this.goodsReceiptLineId = goodsReceiptLineId;
        this.skuId = skuId;
        this.expectedQuantity = expectedQuantity;
        this.receivedQuantity = receivedQuantity;
        this.uom = uom;
        this.batchNumber = batchNumber;
        this.serialNumber = serialNumber;
        this.targetBinId = targetBinId;
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

    public Integer getExpectedQuantity() {
        return expectedQuantity;
    }

    public void setExpectedQuantity(Integer expectedQuantity) {
        this.expectedQuantity = expectedQuantity;
    }

    public Integer getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(Integer receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTargetBinId() {
        return targetBinId;
    }

    public void setTargetBinId(String targetBinId) {
        this.targetBinId = targetBinId;
    }
}