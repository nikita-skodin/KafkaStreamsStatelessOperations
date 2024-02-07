package com.skodin.producer.schedulers;

import com.skodin.producer.models.Message;
import com.skodin.producer.util.generators.MessageGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class KafkaProducerScheduler {

    @Value("${application.kafka.topics.first}")
    private String topicName;

    private final MessageGenerator messageGenerator;
    private final KafkaTemplate<String, Message> firstMessageKafkaTemplate;

    @Scheduled(fixedDelay = 5_00L)
    public void sendToTheFourthTopic() {
        log.info("SENDING TO {}", topicName);
        Message message = messageGenerator.generateMessage();
        log.info("Message: {}", message);
        ProducerRecord<String, Message> record = new ProducerRecord<>(topicName, message);
        record.headers()
                .add("class", Message.class.getSimpleName().getBytes());
        firstMessageKafkaTemplate.send(record)
                .whenComplete((stringEventSendResult, throwable) -> {
                    if (throwable == null) {
                        log.info("SUCCESS!");
                    } else {
                        log.error("ERROR: {}", throwable, throwable);
                    }
                });
    }

}
