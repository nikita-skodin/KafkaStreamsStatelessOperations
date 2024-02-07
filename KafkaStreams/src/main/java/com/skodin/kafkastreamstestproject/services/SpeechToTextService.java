package com.skodin.kafkastreamstestproject.services;

import com.skodin.kafkastreamstestproject.models.ParsedVoiceCommand;
import com.skodin.kafkastreamstestproject.models.VoiceCommand;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class SpeechToTextService {

     public ParsedVoiceCommand speechToText(VoiceCommand voiceCommand){
         // TODO
         return null;
     }

}
