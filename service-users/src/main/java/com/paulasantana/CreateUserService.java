package com.paulasantana;

import br.com.ecommerce.database.LocalDatabase;
import com.paulasantana.common.Message;
import com.paulasantana.consumer.ConsumerService;
import com.paulasantana.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService implements ConsumerService<Order> {

  private LocalDatabase database;

  CreateUserService() throws SQLException {
    this.database = new LocalDatabase("users_database");
    this.database.createIfNotExists("create table Users(" +
        "uuid varchar(200) primary key," +
        "email varchar(200))");
  }

  public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
    new ServiceRunner(CreateUserService::new).start(1);
  }

  public void parse(ConsumerRecord<String, Message<Order>> record) throws InterruptedException, SQLException {
    System.out.println("---------------------------------------------------------------------");
    System.out.println("Processing new order, checking for new user");
    System.out.println(record.value());

    var message = record.value();
    var order = (Order) message.getPayload();

    if (isNewUser(order.getEmail())) {
      insertNewUser(order.getEmail());
    }
  }

  @Override
  public String getTopic() {
    return "ORDER_NEW";
  }

  @Override
  public String getConsumerGroup() {
    return CreateUserService.class.getSimpleName();
  }

  private void insertNewUser(String email) throws SQLException {
    var uuid = UUID.randomUUID().toString();
    this.database.update("insert into Users (uuid, email) " +
        "values (?,?)", uuid, email);

    System.out.println("Usuário uuid e " + email + " adicionado");
  }

  private boolean isNewUser(String email) throws SQLException {
    var results = this.database.query("select uuid from Users where email = ? limit 1", email);
    return !results.next();
  }
}
