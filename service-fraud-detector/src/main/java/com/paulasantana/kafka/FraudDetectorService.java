package com.paulasantana.kafka;

import com.paulasantana.kafka.producer.KafkaDispatcher;
import com.paulasantana.kafka.consumer.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {

    public static void main(String[] args) {
        var fraudService = new FraudDetectorService();
        try(var service = new KafkaService(FraudDetectorService.class.getSimpleName(),
                "ORDER_NEW", fraudService::parse, Order.class,
                new HashMap<String,String>())){
            service.run();
        }

    }

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<Order>();

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException {
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

        var order = record.value();

        if(order.isFraud()){
            System.out.println("Order is a fraud!!!!!");
            orderDispatcher.send("ORDER_REJECTED", order.getEmail(),order);
        }else{
            orderDispatcher.send("ORDER_APPROVED", order.getEmail(),order);
        }

    }

}
