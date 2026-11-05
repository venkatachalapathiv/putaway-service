package com.company.wms.putaway.event;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String GOODS_RECEIVED = "goods-received";
    public static final String PUTAWAY_COMPLETED = "putaway-completed";
    public static final String GOODS_RECEIVED_DLT = "goods-received.DLT";
    public static final String PUTAWAY_COMPLETED_DLT = "putaway-completed.DLT";
}