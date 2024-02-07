package com.skodin.kafkastreamstestproject.util.serdeses;

import com.skodin.kafkastreamstestproject.models.Message;
import com.skodin.kafkastreamstestproject.util.deserializers.MessageDeserializer;
import com.skodin.kafkastreamstestproject.util.serializers.MessageSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageSerde implements Serde<Message> {

    private final MessageDeserializer messageDeserializer;
    private final MessageSerializer messageSerializer;

    @Override
    public Serializer<Message> serializer() {
        return messageSerializer;
    }

    @Override
    public Deserializer<Message> deserializer() {
        return messageDeserializer;
    }
}
