package com.paulasantana;

import com.paulasantana.common.Message;

import com.paulasantana.consumer.KafkaService;
import com.paulasantana.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class EmailNewOrderService {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var emailNewOrderService = new EmailNewOrderService();
        try(var service = new KafkaService(
                EmailNewOrderService.class.getSimpleName(),
                "ORDER_NEW",
                emailNewOrderService::parse,
                new HashMap<String, String>())){
            service.run();
        }
    }

    private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Processing new order, preparing email");
        System.out.println(record.value());

        var message = record.value();

        var order = (Order) message.getPayload();
        var content = "Your Order processing ok";
        var id = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());

        var email = new Email(order.getEmail(), content);

        emailDispatcher.send("ORDER_EMAIL", order.getEmail(), email,id);
    }

}
