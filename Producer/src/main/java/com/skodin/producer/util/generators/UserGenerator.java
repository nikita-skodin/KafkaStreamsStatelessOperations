package com.skodin.producer.util.generators;

import com.skodin.producer.util.UniqueStringTuple;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class UserGenerator {

    private final Random random = new Random();

    private final List<String> names = List.of(
            "Alice", "Bob",
            "Charlie", "David",
            "Eva", "Frank",
            "Grace", "Henry",
            "Ivy", "Jack",
            "Katie", "Leo",
            "Mia", "Nathan",
            "Olivia", "Paul",
            "Quinn", "Ryan",
            "Sophie", "Tom"
    );

    public String generate() {
        int randomIndex = random.nextInt(names.size());
        return names.get(randomIndex);
    }

    public UniqueStringTuple generateTuple() {

        String first = generate();
        String second = generate();

        return first.equals(second) ? generateTuple() : new UniqueStringTuple(first, second);
    }
}
