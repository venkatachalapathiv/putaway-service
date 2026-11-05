
package com.company.wms.putaway.event;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final long sendTimeoutSeconds;

    public KafkaEventPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.outbox.send-timeout-seconds:10}")
            long sendTimeoutSeconds) {

        this.kafkaTemplate = kafkaTemplate;
        this.sendTimeoutSeconds = sendTimeoutSeconds;
    }

    public void publish(
            String topic,
            String key,
            String payload) throws Exception {

        try {
            var result = kafkaTemplate
                    .send(topic, key, payload)
                    .get(sendTimeoutSeconds, TimeUnit.SECONDS);

            log.info(
                    "Kafka acknowledged event: topic={}, partition={}, "
                            + "offset={}, key={}",
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(),
                    key
            );

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();

            log.error(
                    "Interrupted while publishing Kafka event: topic={}, key={}",
                    topic,
                    key,
                    ex
            );

            throw ex;

        } catch (Exception ex) {
            log.error(
                    "Kafka event publishing failed: topic={}, key={}",
                    topic,
                    key,
                    ex
            );

            throw ex;
        }
    }
}
