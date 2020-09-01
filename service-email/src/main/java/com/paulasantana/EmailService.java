package com.paulasantana;

import com.paulasantana.common.Message;
import com.paulasantana.consumer.ConsumerService;
import com.paulasantana.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public class EmailService implements ConsumerService<Email> {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    new ServiceRunner(EmailService::new).start(5);
  }

  public String getConsumerGroup() {
    return EmailService.class.getSimpleName();
  }

  public String getTopic() {
    return "ORDER_EMAIL";
  }

  public void parse(ConsumerRecord<String, Message<Email>> record) {

    System.out.println("---------------------------------------------------------------------");
    System.out.println("Send E-mail");
    System.out.println(record.key());
    System.out.println(record.value());
    System.out.println(record.partition());
    System.out.println(record.offset());

    try {
      Thread.sleep(1000);
    } catch (InterruptedException | IllegalArgumentException e) {
      e.printStackTrace();
    }
    System.out.println("E-mail sent");

  }


}
