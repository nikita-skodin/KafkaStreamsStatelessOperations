package com.skodin.kafkastreamstestproject.processors;

import com.skodin.kafkastreamstestproject.models.VoiceCommand;
import com.skodin.kafkastreamstestproject.services.SpeechToTextService;
import com.skodin.kafkastreamstestproject.services.TranslateService;
import com.skodin.kafkastreamstestproject.util.serdeses.ParsedVoiceCommandSerde;
import com.skodin.kafkastreamstestproject.util.serdeses.VoiceCommandSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class MainProcessor {

    public static final String INPUT_VOICE_COMMANDS = "input-voice-commands";
    public static final String OUTPUT_RECOGNIZED_COMMANDS = "output-recognized-commands";
    public static final String OUTPUT_UNRECOGNIZED_COMMANDS = "output-unrecognized-commands";
    private static final Serde<String> stringSerde = Serdes.String();

    private final SpeechToTextService speechToTextService;
    private final TranslateService translateService;

    private final VoiceCommandSerde voiceCommandSerde;
    private final ParsedVoiceCommandSerde parsedVoiceCommandSerde;

    @Autowired
    public void buildPipelineVoiceCommandParserTopology(StreamsBuilder streamsBuilder) {

        KStream<String, VoiceCommand> messageStream = streamsBuilder
                .stream(INPUT_VOICE_COMMANDS, Consumed.with(stringSerde, voiceCommandSerde));

        var recognizedStreamMap = messageStream
                .filter((key, value) -> value.getAudio().length >= 10)
                .mapValues((readOnlyKey, value) -> speechToTextService.speechToText(value))
                .split(Named.as("branches-"))
                .branch((key, value) -> value.getProbability() >= 0.75, Branched.as("recognized"))
                .defaultBranch(Branched.as("unrecognized"));

        recognizedStreamMap.get("branches-unrecognized")
                .to(OUTPUT_UNRECOGNIZED_COMMANDS, Produced.with(stringSerde, parsedVoiceCommandSerde));

        var languageStreamMap = recognizedStreamMap.get("branches-recognized")
                .split(Named.as("language-"))
                .branch((key, value) -> value.getLanguage().startsWith("en"), Branched.as("english"))
                .defaultBranch(Branched.as("non-english"));

        languageStreamMap
                .get("language-non-english")
                .mapValues(translateService::translate)
                .merge(languageStreamMap.get("language-english"))
                .to(OUTPUT_RECOGNIZED_COMMANDS, Produced.with(stringSerde, parsedVoiceCommandSerde));

        Topology topology = streamsBuilder.build();
        log.info(topology.describe());
    }


}