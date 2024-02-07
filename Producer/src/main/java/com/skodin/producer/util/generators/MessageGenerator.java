package com.skodin.producer.util.generators;

import com.skodin.producer.models.Message;
import com.skodin.producer.util.UniqueStringTuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageGenerator {

    private final UserGenerator userGenerator;
    private final PayloadGenerator payloadGenerator;

    public Message generateMessage() {

        UniqueStringTuple tuple = userGenerator.generateTuple();

        return new Message(
                tuple.first(),
                tuple.second(),
                payloadGenerator.generate()
        );
    }


}
