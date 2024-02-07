package com.skodin.consumer.services;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class KafkaConsumerService {

    @KafkaListener(
            topics = {"${application.kafka.topics.first}"},
            containerFactory = "messageListenerContainerFactory"
    )
    public void listen(List<ConsumerRecord<String, String>> records) {
        for (ConsumerRecord<String, String> record : records) {
            String messageId = record.key();
            String payload = record.value();
            log.info("Message: {}", messageId + ":" + payload);
        }
    }

}
