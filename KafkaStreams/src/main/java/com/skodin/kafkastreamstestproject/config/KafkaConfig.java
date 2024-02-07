package com.skodin.kafkastreamstestproject.config;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Log4j2
@Configuration
public class KafkaConfig {

    @Value("${application.kafka.topics.error}")
    private String topicName;

    @Bean
    public ProducerFactory<Object, Object> errorMessageProducerFactory
            (KafkaProperties kafkaProperties) {

        Map<String, Object> properties = kafkaProperties.buildProducerProperties(new DefaultSslBundleRegistry());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "error-producer");

        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<Object, Object> errorMessageKafkaTemplate
            (ProducerFactory<Object, Object> errorMessageProducerFactory) {
        return new KafkaTemplate<>(errorMessageProducerFactory);
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(topicName).partitions(1).replicas(1).build();
    }

}
