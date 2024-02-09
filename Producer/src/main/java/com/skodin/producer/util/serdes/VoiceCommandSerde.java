package com.skodin.producer.util.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.producer.models.VoiceCommand;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoiceCommandSerde implements Serde<VoiceCommand> {
    private final ObjectMapper objectMapper;

    @Override
    public Serializer<VoiceCommand> serializer() {
        return ((topic, data) -> serializer(data));
    }

    @SneakyThrows
    private byte[] serializer(VoiceCommand data) {
        return objectMapper.writeValueAsString(data).getBytes();
    }

    @Override
    public Deserializer<VoiceCommand> deserializer() {
        return ((topic, data) -> deserializer(data));
    }

    @SneakyThrows
    private VoiceCommand deserializer(byte[] data) {
        return objectMapper.readValue(data, VoiceCommand.class);
    }
}
