package com.skodin.consumer.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, String> messageConsumerFactory
            (KafkaProperties kafkaProperties) {
        return createConsumerFactory(kafkaProperties, new StringDeserializer(), "message_listener");
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> messageListenerContainerFactory(
            ConsumerFactory<String, String> messageConsumerFactory) {
        return createListenerContainerFactory(messageConsumerFactory, 1, "kafka-listener-1");
    }


    private <V> ConsumerFactory<String, V> createConsumerFactory(KafkaProperties kafkaProperties, Deserializer<V> valueDeserializer, String groupId) {
        Map<String, Object> properties = kafkaProperties.buildConsumerProperties(new DefaultSslBundleRegistry());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 3_000);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        DefaultKafkaConsumerFactory<String, V> factory = new DefaultKafkaConsumerFactory<>(properties);
        factory.setValueDeserializer(valueDeserializer);

        return factory;
    }

    private <K, V> KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<K, V>> createListenerContainerFactory(
            ConsumerFactory<K, V> consumerFactory, int concurrency, String executorName) {
        var factory = new ConcurrentKafkaListenerContainerFactory<K, V>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setPollTimeout(1_000);

        ExecutorService pool = Executors.newFixedThreadPool(concurrency, task -> new Thread(task, executorName));
        ConcurrentTaskExecutor executor = new ConcurrentTaskExecutor(pool);
        factory.getContainerProperties().setListenerTaskExecutor(executor);

        return factory;
    }


}
