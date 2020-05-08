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
        try(var dispatcher = new KafkaDispatcher()){
            for (var i =0 ; i < 10; i++){
                var key = UUID.randomUUID().toString();
                var value = "PEDIDO: "+i+ " , "+key;

                dispatcher.send("ORDER_NEW",key,value);

                var email = "Welcome ! We are processing your order";
                dispatcher.send("ORDER_EMAIL", key, email);
            }
        }
    }
}
