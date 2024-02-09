package com.skodin.producer.config;

import com.skodin.producer.models.VoiceCommand;
import com.skodin.producer.util.serdes.VoiceCommandSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
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

    public static final String INPUT_VOICE_COMMANDS = "input-voice-commands";
    public static final String OUTPUT_RECOGNIZED_COMMANDS = "output-recognized-commands";
    public static final String OUTPUT_UNRECOGNIZED_COMMANDS = "output-unrecognized-commands";

    private final VoiceCommandSerde voiceCommandSerde;

    @Bean
    public ProducerFactory<String, VoiceCommand> producerFactory
            (KafkaProperties kafkaProperties) {

        Map<String, Object> properties = kafkaProperties.buildProducerProperties(new DefaultSslBundleRegistry());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "event-producer");

        DefaultKafkaProducerFactory<String, VoiceCommand> factory = new DefaultKafkaProducerFactory<>(properties);

        factory.setValueSerializer(voiceCommandSerde.serializer());

        return factory;
    }

    @Bean
    public KafkaTemplate<String, VoiceCommand> kafkaTemplate
            (ProducerFactory<String, VoiceCommand> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name(INPUT_VOICE_COMMANDS).partitions(1).replicas(1).build();
    }

    public NewTopic topic2() {
        return TopicBuilder.name(OUTPUT_RECOGNIZED_COMMANDS).partitions(1).replicas(1).build();
    }

    public NewTopic topic3() {
        return TopicBuilder.name(OUTPUT_UNRECOGNIZED_COMMANDS).partitions(1).replicas(1).build();
    }

}
