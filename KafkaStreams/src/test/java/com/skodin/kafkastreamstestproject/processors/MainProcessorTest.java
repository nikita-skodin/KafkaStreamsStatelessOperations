package com.skodin.kafkastreamstestproject.processors;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

@ExtendWith(MockitoExtension.class)
class MainProcessorTest {
    Serde<String> STRING_SERDE = Serdes.String();
    final VoiceCommandSerde voiceCommandSerde = new VoiceCommandSerde();
    final ParsedVoiceCommandSerde parsedVoiceCommandSerde = new ParsedVoiceCommandSerde();
    @Mock
    SpeechToTextService speechToTextService;
    @InjectMocks
    MainProcessor mainProcessor;

    @Test
    void buildPipelineVoiceCommandParserTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        mainProcessor.buildPipelineVoiceCommandParserTopology(streamsBuilder);
        Topology topology = streamsBuilder.build();

        try (TopologyTestDriver topologyTestDriver = new TopologyTestDriver(topology, new Properties())) {

            TestInputTopic<String, VoiceCommand> inputTopic = topologyTestDriver.createInputTopic(
                    MainProcessor.INPUT_VOICE_COMMANDS,
                    STRING_SERDE.serializer(), voiceCommandSerde.serializer());

            TestOutputTopic<String, ParsedVoiceCommand> outputTopic = topologyTestDriver.createOutputTopic(
                    MainProcessor.OUTPUT_RECOGNIZED_COMMANDS,
                    STRING_SERDE.deserializer(), parsedVoiceCommandSerde.deserializer());

            var vc = VoiceCommand.builder().id("1").build();
            var ex = new ParsedVoiceCommand("", "", 0.0, "");

            Mockito.when(speechToTextService.speechToText(vc)).thenReturn(ex);

            inputTopic.pipeInput(vc);

            Assertions.assertEquals(ex, outputTopic.readValue());

        }
    }
}