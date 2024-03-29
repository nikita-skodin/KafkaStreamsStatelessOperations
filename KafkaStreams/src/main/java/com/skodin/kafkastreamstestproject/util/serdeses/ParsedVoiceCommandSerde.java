package com.skodin.kafkastreamstestproject.util.serdeses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.kafkastreamstestproject.models.ParsedVoiceCommand;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParsedVoiceCommandSerde implements Serde<ParsedVoiceCommand> {
    private final ObjectMapper objectMapper;

    @Override
    public Serializer<ParsedVoiceCommand> serializer() {
        return ((topic, data) -> serializer(data));
    }

    @SneakyThrows
    private byte[] serializer(ParsedVoiceCommand data) {
        return objectMapper.writeValueAsString(data).getBytes();
    }

    @Override
    public Deserializer<ParsedVoiceCommand> deserializer() {
        return ((topic, data) -> deserializer(data));
    }

    @SneakyThrows
    private ParsedVoiceCommand deserializer(byte[] data) {
        return objectMapper.readValue(data, ParsedVoiceCommand.class);
    }
}
