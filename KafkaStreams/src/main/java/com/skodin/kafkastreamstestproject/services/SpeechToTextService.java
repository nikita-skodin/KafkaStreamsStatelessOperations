package com.skodin.kafkastreamstestproject.services;

import com.skodin.kafkastreamstestproject.models.ParsedVoiceCommand;
import com.skodin.kafkastreamstestproject.models.VoiceCommand;
import org.springframework.stereotype.Component;

@Component
public class SpeechToTextService {

     public ParsedVoiceCommand speechToText(VoiceCommand voiceCommand){
         // TODO
         return ParsedVoiceCommand.builder()
                 .id(voiceCommand.getId())
                 .textCommand("call me")
                 .build();
     }

}
