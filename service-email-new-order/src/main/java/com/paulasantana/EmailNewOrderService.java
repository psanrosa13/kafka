package com.paulasantana;

import com.paulasantana.common.Message;
import com.paulasantana.consumer.ConsumerService;
import com.paulasantana.consumer.ServiceRunner;
import com.paulasantana.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public class EmailNewOrderService implements ConsumerService<Order> {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    new ServiceRunner(EmailNewOrderService::new).start(1);
  }

  private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

  public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
    System.out.println("---------------------------------------------------------------------");
    System.out.println("Processing new order, preparing email");
    System.out.println(record.value());

    var message = record.value();

    var order = (Order) message.getPayload();
    var content = "Your Order processing ok";
    var id = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());

    var email = new Email(order.getEmail(), content);

    emailDispatcher.send("ORDER_EMAIL", order.getEmail(), email, id);
  }

  @Override
  public String getTopic() {
    return "ORDER_NEW";
  }

  @Override
  public String getConsumerGroup() {
    return EmailNewOrderService.class.getSimpleName();
  }

}
