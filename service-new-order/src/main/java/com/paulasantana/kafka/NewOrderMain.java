package com.paulasantana.kafka;

import com.paulasantana.kafka.consumer.KafkaDispatcher;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try(var orderDispatcher = new KafkaDispatcher<Order>()){
            try(var emailDispatcher = new KafkaDispatcher<Email>()) {
                for (var i = 0; i < 10; i++) {

                    var orderId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1);

                    var email = new Email(UUID.randomUUID().toString()+"@gmail.com","Welcome ! We are processing your order");
                    var order = new Order(orderId, amount, email.getSubject());

                    orderDispatcher.send("ORDER_NEW", email.getSubject(), order);


                    emailDispatcher.send("ORDER_EMAIL", email.getSubject(), email);
                }
            }
        }
    }
}
