package com.paulasantana;

import com.paulasantana.common.Message;
import com.paulasantana.consumer.ConsumerService;
import com.paulasantana.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService implements ConsumerService<Order> {

  private final Connection connection;

  private CreateUserService() throws SQLException {
    String url = "jdbc:sqlite:target/users_database.db";
    this.connection = DriverManager.getConnection(url);
    try {
      this.connection.createStatement().execute("create table Users(" +
          "uuid varchar(200) primary key," +
          "email varchar(200))");
    } catch (SQLException e) {
      e.printStackTrace();
    }

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
    var insert = connection.prepareStatement("insert into Users (uuid, email) " +
        "values (?,?)");
    insert.setString(1, UUID.randomUUID().toString());
    insert.setString(2, email);

    insert.execute();

    System.out.println("Usuário uuid e " + email + " adicionado");
  }

  private boolean isNewUser(String email) throws SQLException {
    var query = connection.prepareStatement("select uuid from Users where email = ? limit 1");
    query.setString(1, email);
    var results = query.executeQuery();

    return !results.next();
  }
}
