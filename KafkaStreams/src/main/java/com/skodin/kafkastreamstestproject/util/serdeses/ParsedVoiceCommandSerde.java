package com.skodin.kafkastreamstestproject.util.serdeses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.kafkastreamstestproject.models.ParsedVoiceCommand;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class ParsedVoiceCommandSerde implements Serde<ParsedVoiceCommand> {
    public static final ObjectMapper objectMapper = new ObjectMapper();

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
