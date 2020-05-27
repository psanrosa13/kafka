package com.paulasantana;

import com.paulasantana.common.Message;
import com.paulasantana.consumer.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;

public class EmailService {

    public static void main(String[] args) {
        var emailService = new EmailService();
        var service = new KafkaService( EmailService.class.getSimpleName(),
                "ORDER_EMAIL", emailService::parse, Email.class,
                new HashMap<String,String>());
        service.run();
    }

    private void parse(ConsumerRecord<String, Message<Email>> record) {

        System.out.println("---------------------------------------------------------------------");
        System.out.println("Send E-mail");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());

        try{
            Thread.sleep(1000);
        }catch (InterruptedException | IllegalArgumentException e){
            e.printStackTrace();
        }
        System.out.println("E-mail sent");

    }


}
