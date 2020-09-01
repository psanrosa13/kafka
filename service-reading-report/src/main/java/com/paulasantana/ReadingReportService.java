package com.paulasantana;

import com.paulasantana.common.Message;
import com.paulasantana.consumer.ConsumerService;
import com.paulasantana.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class ReadingReportService implements ConsumerService<User> {

  private static final Path SOURCE = new File("/src/main/resources/report.txt").toPath();

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    new ServiceRunner(ReadingReportService::new).start(5);
  }

  public void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
    System.out.println("---------------------------------------------------------------------");
    System.out.println("Processing report for " + record.value());

    var message = record.value();
    var user = (User) message.getPayload();
    var target = new File(user.getReportPath());
    IO.copyTo(SOURCE, target);
    IO.append(target, "Created for " + user.getUuid());

    System.out.println("File created " + target.getAbsolutePath());
  }

  @Override
  public String getTopic() {
    return "USER_GENERATE_READING_REPORT";
  }

  @Override
  public String getConsumerGroup() {
    return ReadingReportService.class.getSimpleName();
  }

}
