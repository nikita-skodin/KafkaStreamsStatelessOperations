package com.skodin.kafkastreamstestproject.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.kafkastreamstestproject.models.ParsedVoiceCommand;
import com.skodin.kafkastreamstestproject.models.VoiceCommand;
import com.skodin.kafkastreamstestproject.services.SpeechToTextService;
import com.skodin.kafkastreamstestproject.util.serdeses.ParsedVoiceCommandSerde;
import com.skodin.kafkastreamstestproject.util.serdeses.VoiceCommandSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

@ExtendWith(MockitoExtension.class)
class MainProcessorTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private final Serde<String> STRING_SERDE = Serdes.String();
    private final VoiceCommandSerde VOICE_COMMAND_SERDE = new VoiceCommandSerde(mapper);
    private final ParsedVoiceCommandSerde PARSED_VOICE_COMMAND_SERDE = new ParsedVoiceCommandSerde(mapper);

    private final SpeechToTextService speechToTextService = Mockito.mock(SpeechToTextService.class);
    private final MainProcessor mainProcessor = Mockito.spy(new MainProcessor(speechToTextService,
            VOICE_COMMAND_SERDE, PARSED_VOICE_COMMAND_SERDE));

    @Test
    void buildPipelineVoiceCommandParserTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        mainProcessor.buildPipelineVoiceCommandParserTopology(streamsBuilder);
        Topology topology = streamsBuilder.build();

        try (TopologyTestDriver topologyTestDriver = new TopologyTestDriver(topology, new Properties())) {

            TestInputTopic<String, VoiceCommand> inputTopic = topologyTestDriver.createInputTopic(
                    MainProcessor.INPUT_VOICE_COMMANDS,
                    STRING_SERDE.serializer(), VOICE_COMMAND_SERDE.serializer());

            TestOutputTopic<String, ParsedVoiceCommand> outputTopic = topologyTestDriver.createOutputTopic(
                    MainProcessor.OUTPUT_RECOGNIZED_COMMANDS,
                    STRING_SERDE.deserializer(), PARSED_VOICE_COMMAND_SERDE.deserializer());

            String id = "1";
            String language = "ru";

            var vc = VoiceCommand.builder().id(id).language(language).build();
            var pvc = ParsedVoiceCommand.builder().id(id).language(language).build();

            Mockito.when(speechToTextService.speechToText(vc)).thenReturn(pvc);

            inputTopic.pipeInput(vc);

            var actualPvc = outputTopic.readValue();

            Assertions.assertEquals(vc.getId(), actualPvc.getId());
            Assertions.assertEquals(vc.getLanguage(), actualPvc.getLanguage());
        }
    }
}