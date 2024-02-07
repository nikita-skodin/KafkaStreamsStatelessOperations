package com.skodin.kafkastreamstestproject.util.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.kafkastreamstestproject.models.ParsedVoiceCommand;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParseVoiceCommandSerializer implements Serializer<ParsedVoiceCommand> {

    private final ObjectMapper objectMapper;
    @Override
    public byte[] serialize(String topic, ParsedVoiceCommand data) {
        try {
            return objectMapper.writeValueAsString(data).getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
