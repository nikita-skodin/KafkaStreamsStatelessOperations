package com.skodin.kafkastreamstestproject.processors;

import com.skodin.kafkastreamstestproject.models.Message;
import com.skodin.kafkastreamstestproject.util.MessageSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class WordCountProcessor {

    private static final Serde<String> stringSerde = Serdes.String();

    private final MessageSerde messageSerde;

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        KStream<String, Message> messageStream = streamsBuilder
                .stream("input-topic", Consumed.with(stringSerde, messageSerde));

        messageStream
                .map((key, value) -> new KeyValue<>(value.from() + ":" + value.to(), value.payload()))
                .to("output-topic");

        Topology topology = streamsBuilder.build();
        log.info(topology.describe());
    }
}