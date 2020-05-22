package com.paulasantana.kafka;

import com.paulasantana.kafka.common.Message;
import com.paulasantana.kafka.consumer.KafkaService;
import com.paulasantana.kafka.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class ReadingReportService {

    private static final Path SOURCE = new File("/src/main/resources/report.txt").toPath();

    public static void main(String[] args) {
        var readingReportService = new ReadingReportService();
        try(var service = new KafkaService(ReadingReportService.class.getSimpleName(),
                "USER_GENERATE_READING_REPORT", readingReportService::parse, User.class,
                new HashMap<String,String>())){
            service.run();
        }

    }

    private void parse(ConsumerRecord<String, Message<User>> record) throws ExecutionException, InterruptedException, IOException {
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Processing report for "+ record.value());

        var message = record.value();
        var user = (User) message.getPayload();
        var target = new File(user.getReportPath());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for "+user.getUuid());

        System.out.println("File created "+target.getAbsolutePath());
    }

}
