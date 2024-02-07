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

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class WordCountProcessor {

    private static final Serde<String> stringSerde = Serdes.String();

    private final MessageSerde messageSerde;

    @Autowired
    public void buildPipelineForEmptyStatelessMapValue(StreamsBuilder streamsBuilder) {
        KStream<String, Message> messageStream = streamsBuilder
                .stream("input-topic", Consumed.with(stringSerde, messageSerde));

        messageStream
                .map((key, value) -> new KeyValue<>(value.from() + ":" + value.to(), value.payload()))
                .to("output-topic");

        Topology topology = streamsBuilder.build();
        log.info(topology.describe());
    }

    @Autowired
    public void buildPipelineForEmptyWordCounter(StreamsBuilder streamsBuilder) {
        KStream<String, String> messageStream = streamsBuilder
                .stream("input-words-topic", Consumed.with(stringSerde, stringSerde));

        messageStream
                .flatMapValues(((readOnlyKey, value) -> List.of(value.toLowerCase().split(" "))))
                .groupBy((key, value) -> value)
                .count()
                .toStream()
                .to("output-word-topic");

        Topology topology = streamsBuilder.build();
        log.info(topology.describe());
    }



}