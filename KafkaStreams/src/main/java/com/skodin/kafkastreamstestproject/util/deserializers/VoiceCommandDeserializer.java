package com.skodin.kafkastreamstestproject.util.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.kafkastreamstestproject.models.VoiceCommand;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class VoiceCommandDeserializer implements Deserializer<VoiceCommand> {
    private final ObjectMapper objectMapper;

    @Override
    public VoiceCommand deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, VoiceCommand.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public VoiceCommand deserialize(String topic, Headers headers, byte[] data) {
        for (var header : headers.headers("class")) {
            if (Arrays.equals(header.value(), "VoiceCommand".getBytes())) {
                return Deserializer.super.deserialize(topic, headers, data);
            }
        }

        throw new RuntimeException("Invalid type");
    }
}