package com.skodin.producer.util.generators;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class PayloadGenerator {

    private final Random random = new Random();

    private final List<String> messages = List.of(
            "Hello",
            "How are you?",
            "I love programming",
            "Kafka is awesome",
            "Spring Boot",
            "OpenAI is amazing",
            "Coding is fun",
            "Learning new things",
            "Happy coding!",
            "Java is versatile"
    );

    public String generate() {
        int randomIndex = random.nextInt(messages.size());
        return messages.get(randomIndex);
    }
}
