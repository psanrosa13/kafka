package com.paulasantana;

import com.paulasantana.common.Message;
import com.paulasantana.consumer.KafkaService;
import com.paulasantana.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    var fraudService = new FraudDetectorService();
    try (var service = new KafkaService(FraudDetectorService.class.getSimpleName(),
        "ORDER_NEW", fraudService::parse,
        new HashMap<String, String>())) {
      service.run();
    }

  }

  private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<Order>();

  private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
    System.out.println("---------------------------------------------------------------------");
    System.out.println("Processing new order, checking for fraud");
    System.out.println(record.key());
    System.out.println(record.value());
    System.out.println(record.partition());
    System.out.println(record.offset());

    try {
      Thread.sleep(5000);
    } catch (InterruptedException | IllegalArgumentException e) {
      e.printStackTrace();
    }

    var message = record.value();
    var order = (Order) message.getPayload();

    if (order.isFraud()) {
      System.out.println("Order is a fraud!!!!!");
      orderDispatcher.send("ORDER_REJECTED", order.getEmail(), order, message.getId().continueWith(FraudDetectorService.class.getSimpleName()));
    } else {
      orderDispatcher.send("ORDER_APPROVED", order.getEmail(), order, message.getId().continueWith(FraudDetectorService.class.getSimpleName()));
    }

  }

}
