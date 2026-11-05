package com.company.wms.putaway.event;

public enum PutAwayEventType {

    GOODS_RECEIVED("GoodsReceived"),
    PUTAWAY_COMPLETED("PutAwayCompleted");

    private final String value;

    PutAwayEventType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}