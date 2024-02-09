package com.skodin.kafkastreamstestproject.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.kafkastreamstestproject.models.ParsedVoiceCommand;
import com.skodin.kafkastreamstestproject.models.VoiceCommand;
import com.skodin.kafkastreamstestproject.services.SpeechToTextService;
import com.skodin.kafkastreamstestproject.services.TranslateService;
import com.skodin.kafkastreamstestproject.util.serdeses.ParsedVoiceCommandSerde;
import com.skodin.kafkastreamstestproject.util.serdeses.VoiceCommandSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;
import java.util.Random;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MainProcessorTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private final Serde<String> STRING_SERDE = Serdes.String();
    private final VoiceCommandSerde VOICE_COMMAND_SERDE = new VoiceCommandSerde(mapper);
    private final ParsedVoiceCommandSerde PARSED_VOICE_COMMAND_SERDE = new ParsedVoiceCommandSerde(mapper);

    private final SpeechToTextService speechToTextService = mock(SpeechToTextService.class);
    private final TranslateService translateService = mock(TranslateService.class);

    private final MainProcessor mainProcessor = spy(new MainProcessor(speechToTextService, translateService,
            VOICE_COMMAND_SERDE, PARSED_VOICE_COMMAND_SERDE));

    private TestInputTopic<String, VoiceCommand> inputTopic;
    private TestOutputTopic<String, ParsedVoiceCommand> outputRecognizedTopic;
    private TestOutputTopic<String, ParsedVoiceCommand> outputUnrecognizedTopic;

    private VoiceCommand vc;
    private ParsedVoiceCommand pvc;
    private ParsedVoiceCommand tpvc;

    private TopologyTestDriver buildPipelineVoiceCommandParserTopologyInit() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        mainProcessor.buildPipelineVoiceCommandParserTopology(streamsBuilder);
        Topology topology = streamsBuilder.build();

        TopologyTestDriver topologyTestDriver = new TopologyTestDriver(topology, new Properties());

        inputTopic = topologyTestDriver.createInputTopic(
                MainProcessor.INPUT_VOICE_COMMANDS,
                STRING_SERDE.serializer(), VOICE_COMMAND_SERDE.serializer());

        outputRecognizedTopic = topologyTestDriver.createOutputTopic(
                MainProcessor.OUTPUT_RECOGNIZED_COMMANDS,
                STRING_SERDE.deserializer(), PARSED_VOICE_COMMAND_SERDE.deserializer());

        outputUnrecognizedTopic = topologyTestDriver.createOutputTopic(
                MainProcessor.OUTPUT_UNRECOGNIZED_COMMANDS,
                STRING_SERDE.deserializer(), PARSED_VOICE_COMMAND_SERDE.deserializer());

        var bytes = new byte[20];
        new Random(1).nextBytes(bytes);
        String id = "1";
        String firstLanguage = "en";
        String secondLanguage = "en";
        Double probability = 0.75;

        vc = VoiceCommand.builder()
                .id(id)
                .language(firstLanguage)
                .audio(bytes)
                .build();

        pvc = ParsedVoiceCommand.builder()
                .id(id)
                .language(secondLanguage)
                .textCommand("Позвони мне")
                .probability(probability)
                .build();

        tpvc = ParsedVoiceCommand.builder()
                .id(id)
                .textCommand("call me")
                .probability(probability)
                .language(secondLanguage)
                .build();

        return topologyTestDriver;
    }

    @Test
    void buildPipelineVoiceCommandParserTopology_EnglishVoiceCommandAndCorrectlyProcess_ReceiveToOutputTopic() {
        try (TopologyTestDriver driver = buildPipelineVoiceCommandParserTopologyInit()) {

            when(speechToTextService.speechToText(vc)).thenReturn(pvc);

            inputTopic.pipeInput(vc);

            var actualPvc = outputRecognizedTopic.readValue();

            Assertions.assertTrue(outputUnrecognizedTopic.isEmpty());
            Assertions.assertEquals(pvc.getId(), actualPvc.getId());
            Assertions.assertEquals(pvc.getLanguage(), actualPvc.getLanguage());
            Assertions.assertEquals(pvc.getTextCommand(), actualPvc.getTextCommand());
        }
    }

    @Test
    void buildPipelineVoiceCommandParserTopology_NonEnglishVoiceCommandAndCorrectlyProcess_ReceiveToOutputTopic() {
        try (TopologyTestDriver driver = buildPipelineVoiceCommandParserTopologyInit()) {

            pvc.setLanguage("ru");
            when(speechToTextService.speechToText(vc)).thenReturn(pvc);
            when(translateService.translate(pvc)).thenReturn(tpvc);

            inputTopic.pipeInput(vc);

            var actualVoiceCommand = outputRecognizedTopic.readRecord().getValue();
            Assertions.assertTrue(outputUnrecognizedTopic.isEmpty());
            Assertions.assertEquals(vc.getId(), actualVoiceCommand.getId());
            Assertions.assertEquals("call me", actualVoiceCommand.getTextCommand());
        }
    }

    @Test
    void buildPipelineVoiceCommandParserTopology_NonRecognizableVoiceCommand_ReceiveToUnrecognizedTopic() {
        try (TopologyTestDriver driver = buildPipelineVoiceCommandParserTopologyInit()) {

            pvc.setProbability(0.60);
            when(speechToTextService.speechToText(vc)).thenReturn(pvc);

            inputTopic.pipeInput(vc);

            var actualVoiceCommand = outputUnrecognizedTopic.readRecord().getValue();
            Assertions.assertEquals(vc.getId(), actualVoiceCommand.getId());
            Assertions.assertTrue(outputRecognizedTopic.isEmpty());
            verify(translateService, never()).translate(any(ParsedVoiceCommand.class));
        }
    }

    @Test
    void buildPipelineVoiceCommandParserTopology_VoiceCommandLessThen10Bytes_ReceiveToNowhere() {
        try (TopologyTestDriver driver = buildPipelineVoiceCommandParserTopologyInit()) {

            vc.setAudio(new byte[]{1, 2, 3});
            inputTopic.pipeInput(vc);

            Assertions.assertTrue(outputRecognizedTopic.isEmpty());
            Assertions.assertTrue(outputUnrecognizedTopic.isEmpty());
            verify(translateService, never()).translate(any(ParsedVoiceCommand.class));
            verify(speechToTextService, never()).speechToText(any(VoiceCommand.class));
        }
    }

}