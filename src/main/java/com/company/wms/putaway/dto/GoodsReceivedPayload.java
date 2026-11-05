package com.company.wms.putaway.dto;

import java.util.List;

public class GoodsReceivedPayload {

    private String goodsReceiptId;
    private String receiptNumber;
    private String warehouseId;
    private List<GoodsReceivedLine> lines;

    public GoodsReceivedPayload() {
    }

    public GoodsReceivedPayload(String goodsReceiptId,
                                String receiptNumber,
                                String warehouseId,
                                List<GoodsReceivedLine> lines) {
        this.goodsReceiptId = goodsReceiptId;
        this.receiptNumber = receiptNumber;
        this.warehouseId = warehouseId;
        this.lines = lines;
    }

    public String getGoodsReceiptId() {
        return goodsReceiptId;
    }

    public void setGoodsReceiptId(String goodsReceiptId) {
        this.goodsReceiptId = goodsReceiptId;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public List<GoodsReceivedLine> getLines() {
        return lines;
    }

    public void setLines(List<GoodsReceivedLine> lines) {
        this.lines = lines;
    }
}