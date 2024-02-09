package com.skodin.producer.schedulers;

import com.skodin.producer.models.VoiceCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Log4j2
@Component
@RequiredArgsConstructor
public class KafkaProducerScheduler {

    public static final String INPUT_VOICE_COMMANDS = "input-voice-commands";

    private final KafkaTemplate<String, VoiceCommand> kafkaTemplate;

    @Scheduled(fixedDelay = 1_000L)
    public void sendToTheFourthTopic1() {
        log.info("SENDING TO {}", INPUT_VOICE_COMMANDS);
        ProducerRecord<String, VoiceCommand> record = new ProducerRecord<>(INPUT_VOICE_COMMANDS,
                getVoiceCommand());
        kafkaTemplate.send(record)
                .whenComplete((stringEventSendResult, throwable) -> {
                    if (throwable == null) {
                        log.info("SUCCESS!");
                    } else {
                        log.error("ERROR: {}", throwable, throwable);
                    }
                });
    }

    private VoiceCommand getVoiceCommand() {
        List<VoiceCommand> voiceCommandList = Arrays.asList(
                VoiceCommand.builder()
                        .id("26679943-f55e-4731-986e-c5c5395715de") // output-recognized-commands
                        .audio("morethen10.".getBytes())
                        .audioCodec("FLAC")
                        .language("en-US")
                        .build(),
                VoiceCommand.builder()  // ignore
                        .id("9821f112-ec35-4679-91e7-c558de479bc5")
                        .audio("less10".getBytes())
                        .audioCodec("FLAC")
                        .language("es-AR")
                        .build(),
                VoiceCommand.builder()
                        .id("e0b80c6a-5c59-479c-b8d4-0b3f375e8b19") // output-unrecognized-commands
                        .audio("morethen10.".getBytes())
                        .audioCodec("FLAC")
                        .language("en-US")
                        .build()
        );

        VoiceCommand voiceCommand = voiceCommandList.get(new Random().nextInt(3));
        log.info("send: {}", voiceCommand);

        return voiceCommand;
    }


}
