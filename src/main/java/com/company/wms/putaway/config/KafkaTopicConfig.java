package com.company.wms.putaway.config;

import com.company.wms.putaway.event.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic goodsReceivedTopic() {
        return TopicBuilder.name(KafkaTopics.GOODS_RECEIVED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic putAwayCompletedTopic() {
        return TopicBuilder.name(KafkaTopics.PUTAWAY_COMPLETED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic goodsReceivedDltTopic() {
        return TopicBuilder.name(KafkaTopics.GOODS_RECEIVED_DLT)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic putAwayCompletedDltTopic() {
        return TopicBuilder.name(KafkaTopics.PUTAWAY_COMPLETED_DLT)
                .partitions(3)
                .replicas(1)
                .build();
    }
}