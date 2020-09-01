package com.paulasantana.consumer;

import com.paulasantana.common.Message;
import com.paulasantana.producer.ConsumerFunction;
import com.paulasantana.producer.GsonSerializer;
import com.paulasantana.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {
  private final KafkaConsumer<String, Message<T>> consumer;
  private final ConsumerFunction<T> parse;

  public KafkaService(String groupId, String topic, ConsumerFunction<T> parse, Map<String, String> properties) {
    this.parse = parse;
    this.consumer = new KafkaConsumer<String, Message<T>>(getProperties(groupId, properties));
    consumer.subscribe(Collections.singletonList(topic));
  }

  public KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Map<String, String> properties) {
    this.parse = parse;
    this.consumer = new KafkaConsumer<String, Message<T>>(getProperties(groupId, properties));
    consumer.subscribe(topic);
  }

  public void run() throws ExecutionException, InterruptedException {
    try (var dispatcher = new KafkaDispatcher<>()) {
      while (true) {
        var records = consumer.poll(Duration.ofMillis(100));
        if (!records.isEmpty()) {
          System.out.println("Encontrei " + records.count() + " registros");
        } else {
          continue;
        }
        for (var record : records) {
          try {
            parse.consume(record);
          } catch (Exception e) {
            var message = record.value();
            dispatcher.send("ECOMMERCE_DEADLETTER", message.getId().toString(),
                new GsonSerializer<>().serialize("", message),
                message.getId().continueWith("DeadLetter"));
          }
        }
      }
    }

  }

  private Properties getProperties(String groupId, Map<String, String> overrideProperties) {
    var properties = new Properties();

    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
    properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    properties.putAll(overrideProperties);

    return properties;
  }

  @Override
  public void close() {
    consumer.close();
  }
}
