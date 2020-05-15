package br.com.paulasantana.kafka;

import com.paulasantana.kafka.consumer.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private final Connection connection;

    CreateUserService() throws SQLException {
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
        var createUserService = new CreateUserService();
        try(var service = new KafkaService(CreateUserService.class.getSimpleName(),
                "ORDER_NEW", createUserService::parse, Order.class,
                new HashMap<String,String>())){
            service.run();
        }

    }

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());

         var order = record.value();

         if(isNewUser(order.getEmail())){
            insertNewUser(order.getEmail());
         }
    }

    private void insertNewUser(String email) throws SQLException {
        var insert = connection.prepareStatement("insert into Users (uuid, email) " +
                "values (?,?)");
        insert.setString(1, UUID.randomUUID().toString());
        insert.setString(2, email);

        insert.execute();

        System.out.println("Usuário uuid e "+email+" adicionado");
    }

    private boolean isNewUser(String email) throws SQLException {
        var query = connection.prepareStatement("select uuid from Users where email = ? limit 1");
        query.setString(1, email);
        var results = query.executeQuery();

        return !results.next();
    }
}
