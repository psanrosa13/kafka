package com.paulasantana;

import br.com.ecommerce.database.LocalDatabase;
import com.paulasantana.common.Message;
import com.paulasantana.consumer.ConsumerService;
import com.paulasantana.consumer.ServiceRunner;
import com.paulasantana.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService implements ConsumerService<Order> {

  private LocalDatabase database;

  FraudDetectorService() throws SQLException {
    this.database = new LocalDatabase("frauds_database");
    this.database.createIfNotExists("create table Orders(" +
        "uuid varchar(200) primary key," +
        "is_fraud boolean)");
  }

  public static void main(String[] args) {
    new ServiceRunner<>(FraudDetectorService::new).start(1);
  }

  @Override
  public String getTopic() {
    return "ORDER_NEW";
  }

  @Override
  public String getConsumerGroup() {
    return FraudDetectorService.class.getSimpleName();
  }

  private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<Order>();

  public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
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
    if (wasProcessed(order)) {
      System.out.println("Order " + order.getOrderId() + " was a already processed");
    }

    if (order.isFraud()) {
      database.update("insert into Orders (uuid, is_fraud) values (?, true)", order.getOrderId());
      System.out.println("Order is a fraud!!!!!");
      orderDispatcher.send("ORDER_REJECTED", order.getEmail(), order, message.getId().continueWith(FraudDetectorService.class.getSimpleName()));
    } else {
      database.update("insert into Orders (uuid, is_fraud) values (?, false)", order.getOrderId());
      orderDispatcher.send("ORDER_APPROVED", order.getEmail(), order, message.getId().continueWith(FraudDetectorService.class.getSimpleName()));
    }

  }

  private boolean wasProcessed(Order order) throws SQLException {
    var results = database.query("select uuid from Orders where uuid = ? limit 1", order.getOrderId());
    return results.next();
  }

}
