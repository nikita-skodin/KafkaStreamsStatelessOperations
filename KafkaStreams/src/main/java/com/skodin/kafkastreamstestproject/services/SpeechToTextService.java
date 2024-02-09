package com.skodin.kafkastreamstestproject.services;

import com.skodin.kafkastreamstestproject.models.ParsedVoiceCommand;
import com.skodin.kafkastreamstestproject.models.VoiceCommand;
import org.springframework.stereotype.Component;

@Component
public class SpeechToTextService {

     public ParsedVoiceCommand speechToText(VoiceCommand value){
         return switch (value.getId()) {
             case "26679943-f55e-4731-986e-c5c5395715de" -> ParsedVoiceCommand.builder()    // output-recognized-commands
                     .id(value.getId())
                     .textCommand("call john")
                     .probability(0.957)
                     .language(value.getLanguage())
                     .build();
             case "9821f112-ec35-4679-91e7-c558de479bc5" -> ParsedVoiceCommand.builder()
                     .id(value.getId())
                     .textCommand("llamar a juan")
                     .probability(0.937)
                     .language(value.getLanguage())
                     .build();
             default -> ParsedVoiceCommand.builder()    // output-unrecognized-commands
                     .id(value.getId())
                     .textCommand("call john")
                     .probability(0.37)
                     .language(value.getLanguage())
                     .build();
         };
     }

}
