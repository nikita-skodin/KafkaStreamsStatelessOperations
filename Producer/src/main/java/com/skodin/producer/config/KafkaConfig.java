package com.skodin.producer.config;

import com.skodin.producer.models.Message;
import com.skodin.producer.util.serializers.MessageSerializer;
import lombok.RequiredArgsConstructor;
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

import java.util.Map;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${application.kafka.topics.first}")
    private String topicName;

    private final MessageSerializer messageSerializer;

    @Bean
    public ProducerFactory<String, Message> firstMessageProducerFactory
            (KafkaProperties kafkaProperties) {

        Map<String, Object> properties = kafkaProperties.buildProducerProperties(new DefaultSslBundleRegistry());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "message-producer");

        DefaultKafkaProducerFactory<String, Message> factory = new DefaultKafkaProducerFactory<>(properties);

        factory.setValueSerializer(messageSerializer);

        return factory;
    }

    @Bean
    public KafkaTemplate<String, Message> firstMessageKafkaTemplate
            (ProducerFactory<String, Message> firstMessageProducerFactory) {
        return new KafkaTemplate<>(firstMessageProducerFactory);
    }

    @Bean
    public NewTopic firstTopic() {
        return TopicBuilder.name(topicName).partitions(1).replicas(1).build();
    }

}
