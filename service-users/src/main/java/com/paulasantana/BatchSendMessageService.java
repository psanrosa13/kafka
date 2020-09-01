package com.paulasantana;

import com.paulasantana.common.Message;
import com.paulasantana.consumer.KafkaService;
import com.paulasantana.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {

  private final Connection connection;

  BatchSendMessageService() throws SQLException {
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
    var batchSendMessageService = new BatchSendMessageService();
    try (var service = new KafkaService(BatchSendMessageService.class.getSimpleName(),
        "SEND_MESSAGE_TO_ALL_USERS", batchSendMessageService::parse, Map.of())) {
      service.run();
    }

  }

  private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<User>();

  private void parse(ConsumerRecord<String, Message<String>> record) throws ExecutionException, InterruptedException, SQLException {
    System.out.println("---------------------------------------------------------------------");
    System.out.println("Processing new batch, checking for new user");
    var message = record.value();
    System.out.println("TOPIC : " + message.getPayload());

    for (User user : getAllUsers()) {
      userDispatcher.send((String) message.getPayload(), user.getUuid(), user,
          message.getId().continueWith(BatchSendMessageService.class.getSimpleName()));
    }
  }

  private List<User> getAllUsers() throws SQLException {
    var query = connection.prepareStatement("select uuid from Users ");
    var results = query.executeQuery();
    List<User> users = new ArrayList<>();

    while (results.next()) {
      users.add(new User(results.getString(1)));
    }
    return users;
  }
}
