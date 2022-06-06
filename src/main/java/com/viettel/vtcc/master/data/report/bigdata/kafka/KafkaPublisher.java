package com.viettel.vtcc.master.data.report.bigdata.kafka;


import com.viettel.vtcc.master.data.utils.ConfigurationLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

@Slf4j
public class KafkaPublisher {
    private static final String SERVER_KAFKA = ConfigurationLoader.getInstance().getAsString("server.kafka", "localhost:29092");
    private static Producer<String, String> producer;

    public KafkaPublisher() {
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers", SERVER_KAFKA);

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
        props.put("retries", 2);

        //Specify buffer size in config
//        props.put("batch.size", 16384);
//
//        //Reduce the no of requests less than 0
//        props.put("linger.ms", 1);
//
//        //The buffer.memory controls the total amount of memory available to the producer for buffering.
//        props.put("buffer.memory", 33554432);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
    }


    public void push_data(String data, String topic_name) {
        producer.send(new ProducerRecord<>(topic_name, data), (recordMetadata, e) -> {
            log.info("message send success {}", recordMetadata.topic());
        });
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        log.info("push data to kafka {}", data);
    }
}

