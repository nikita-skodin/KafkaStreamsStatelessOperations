package com.skodin.kafkastreamstestproject.processors;

import com.skodin.kafkastreamstestproject.models.VoiceCommand;
import com.skodin.kafkastreamstestproject.services.SpeechToTextService;
import com.skodin.kafkastreamstestproject.util.serdeses.MessageSerde;
import com.skodin.kafkastreamstestproject.util.serdeses.ParsedVoiceCommandSerde;
import com.skodin.kafkastreamstestproject.util.serdeses.VoiceCommandSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class MainProcessor {

    public static final String INPUT_VOICE_COMMANDS = "input-voice-commands";
    public static final String OUTPUT_RECOGNIZED_COMMANDS = "output-recognized-commands";
    private static final Serde<String> stringSerde = Serdes.String();

    private final VoiceCommandSerde voiceCommandSerde;
    private final ParsedVoiceCommandSerde parsedVoiceCommandSerde;

    private final SpeechToTextService speechToTextService;

    private final MessageSerde messageSerde;

//    @Autowired
//    public void buildPipelineForEmptyStatelessMapValue(StreamsBuilder streamsBuilder) {
//        KStream<String, Message> messageStream = streamsBuilder
//                .stream("input-topic", Consumed.with(stringSerde, messageSerde));
//
//        messageStream
//                .map((key, value) -> new KeyValue<>(value.from() + ":" + value.to(), value.payload()))
//                .to("output-topic");
//
//        Topology topology = streamsBuilder.build();
//        log.info(topology.describe());
//    }
//
//    @Autowired
//    public void buildPipelineForEmptyWordCounter(StreamsBuilder streamsBuilder) {
//        KStream<String, String> messageStream = streamsBuilder
//                .stream("input-words-topic", Consumed.with(stringSerde, stringSerde));
//
//        messageStream
//                .flatMapValues(((readOnlyKey, value) -> List.of(value.toLowerCase().split(" "))))
//                .groupBy((key, value) -> value)
//                .count()
//                .toStream()
//                .to("output-word-topic");
//
//        Topology topology = streamsBuilder.build();
//        log.info(topology.describe());
//    }

    @Autowired
    public void buildPipelineVoiceCommandParserTopology(StreamsBuilder streamsBuilder) {

        KStream<String, VoiceCommand> messageStream = streamsBuilder
                .stream(INPUT_VOICE_COMMANDS, Consumed.with(stringSerde, voiceCommandSerde));

        messageStream
                .mapValues((readOnlyKey, value) -> speechToTextService.speechToText(value))
                // TODO
                .to(OUTPUT_RECOGNIZED_COMMANDS, Produced.with(stringSerde, parsedVoiceCommandSerde));

        Topology topology = streamsBuilder.build();
        log.info(topology.describe());
    }


}