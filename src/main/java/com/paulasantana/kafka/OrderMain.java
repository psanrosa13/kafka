package com.paulasantana.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class OrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var producer = new KafkaProducer<String, String>(properties());
        var key = UUID.randomUUID().toString();
        var value = "pedido5,450";
        var record = new ProducerRecord<>("ORDER_NEW", key, value);
        Callback callback = (data, ex) -> {

            if (ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println("Topic - " + data.topic() + " / Partition - " + data.partition() + " / Offset - " + data.offset() + " / Timestamp - " + data.timestamp());
        };
        producer.send(record, callback).get();
        var email = "Welcome ! We are processing your order";
        var emailRecord = new ProducerRecord<>("ORDER_EMAIL", key, email);

        producer.send(emailRecord, callback).get();


    }

    private static Properties properties() {
        var properties = new Properties();

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());


        return properties;
    }

}
