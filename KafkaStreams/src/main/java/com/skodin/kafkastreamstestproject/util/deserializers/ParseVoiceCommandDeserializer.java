package com.skodin.kafkastreamstestproject.util.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.kafkastreamstestproject.models.ParsedVoiceCommand;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ParseVoiceCommandDeserializer implements Deserializer<ParsedVoiceCommand> {

    private final ObjectMapper objectMapper;

    @Override
    public ParsedVoiceCommand deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, ParsedVoiceCommand.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    @Override
//    public ParsedVoiceCommand deserialize(String topic, Headers headers, byte[] data) {
//        for (var header : headers.headers("class")) {
//            if (Arrays.equals(header.value(), "ParseVoiceCommand".getBytes())) {
//                return Deserializer.super.deserialize(topic, headers, data);
//            }
//        }
//
//        throw new RuntimeException("Invalid type");
//    }
}
