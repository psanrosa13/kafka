package br.com.paulasantana.kafka;

import com.paulasantana.kafka.consumer.KafkaService;
import com.paulasantana.kafka.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {

    private final Connection connection;

    BatchSendMessageService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        try{
            this.connection.createStatement().execute("create table Users(" +
                    "uuid varchar(200) primary key," +
                    "email varchar(200))");
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws SQLException {
        var batchSendMessageService = new BatchSendMessageService();
        try(var service = new KafkaService(BatchSendMessageService.class.getSimpleName(),
                "SEND_MESSAGE_TO_ALL_USERS", batchSendMessageService::parse, String.class,Map.of())){
            service.run();
        }

    }

    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<User>();

    private void parse(ConsumerRecord<String, String> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Processing new batch, checking for new user");
        System.out.println("TOPIC : "+record.value());

        for (User user: getAllUsers()) {
            userDispatcher.send( record.value(),  user.getUuid(), user);
        }
    }

    private List<User> getAllUsers() throws SQLException {
        var query = connection.prepareStatement("select uuid from Users ");
        var results = query.executeQuery();
        List<User> users = new ArrayList<>();

        while(results.next()){
            users.add(new User(results.getString(1)));
        }
        return users;
    }
}
