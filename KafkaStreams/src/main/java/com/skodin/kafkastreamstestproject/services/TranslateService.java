package com.skodin.kafkastreamstestproject.services;

import com.skodin.kafkastreamstestproject.models.ParsedVoiceCommand;
import org.springframework.stereotype.Component;

@Component
public class TranslateService {

    public ParsedVoiceCommand translate(ParsedVoiceCommand original) {
        return ParsedVoiceCommand.builder()
                .id(original.getId())
                .textCommand("call juan")
                .probability(original.getProbability())
                .language(original.getLanguage())
                .build();
    }

}
