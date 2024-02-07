package com.skodin.kafkastreamstestproject.util.serdeses;

import com.skodin.kafkastreamstestproject.models.Message;
import com.skodin.kafkastreamstestproject.models.VoiceCommand;
import com.skodin.kafkastreamstestproject.util.deserializers.VoiceCommandDeserializer;
import com.skodin.kafkastreamstestproject.util.serializers.VoiceCommandSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoiceCommandSerde implements Serde<VoiceCommand> {
    private final VoiceCommandSerializer voiceCommandSerializer;
    private final VoiceCommandDeserializer voiceCommandDeserializer;
    @Override
    public Serializer<VoiceCommand> serializer() {
        return voiceCommandSerializer;
    }

    @Override
    public Deserializer<VoiceCommand> deserializer() {
        return voiceCommandDeserializer;
    }
}
