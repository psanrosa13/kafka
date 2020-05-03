package com.paulasantana.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {

    public static void main(String[] args)  {
        var consumer = new KafkaConsumer<String, String>(properties());
        consumer.subscribe(Collections.singletonList("NEW_ORDER"));
        while(true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (records.isEmpty()) {
                System.out.println("Não encontrei registros");
                continue;
            }
            for (var record : records) {
                System.out.println("---------------------------------------------------------------------");
                System.out.println("Processing new order, checking for fraud");
                System.out.println(record.key());
                System.out.println(record.value());
                System.out.println(record.partition());
                System.out.println(record.offset());

                try{
                    Thread.sleep(5000);
                }catch (InterruptedException | IllegalArgumentException e){
                    e.printStackTrace();
                }
                System.out.println("Oder processed");
            }
        }

    }

    private static Properties properties() {
        var properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, FraudDetectorService.class.getName());

        return properties;
    }
}
