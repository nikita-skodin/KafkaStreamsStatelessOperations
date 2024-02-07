package com.skodin.kafkastreamstestproject.util.serdeses;

import com.skodin.kafkastreamstestproject.models.ParsedVoiceCommand;
import com.skodin.kafkastreamstestproject.util.deserializers.ParseVoiceCommandDeserializer;
import com.skodin.kafkastreamstestproject.util.serializers.ParseVoiceCommandSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParsedVoiceCommandSerde implements Serde<ParsedVoiceCommand> {

    private final ParseVoiceCommandSerializer parseVoiceCommandSerializer;
    private final ParseVoiceCommandDeserializer parseVoiceCommandDeserializer;

    @Override
    public Serializer<ParsedVoiceCommand> serializer() {
        return parseVoiceCommandSerializer;
    }

    @Override
    public Deserializer<ParsedVoiceCommand> deserializer() {
        return parseVoiceCommandDeserializer;
    }
}
