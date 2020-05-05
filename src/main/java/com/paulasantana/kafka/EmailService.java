package com.paulasantana.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class EmailService {

    public static void main(String[] args)  {
        var consumer = new KafkaConsumer<String, String>(properties());
        consumer.subscribe(Collections.singletonList("ORDER_EMAIL"));
        while(true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (records.isEmpty()) {
                System.out.println("Não encontrei registros");
                continue;
            }
            for (var record : records) {
                System.out.println("---------------------------------------------------------------------");
                System.out.println("Send E-mail");
                System.out.println(record.key());
                System.out.println(record.value());
                System.out.println(record.partition());
                System.out.println(record.offset());

                try{
                    Thread.sleep(1000);
                }catch (InterruptedException | IllegalArgumentException e){
                    e.printStackTrace();
                }
                System.out.println("E-mail sent");
            }
        }

    }

    private static Properties properties() {
        var properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, EmailService.class.getName());

        return properties;
    }
}
