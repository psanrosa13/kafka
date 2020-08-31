package com.paulasantana.consumer;

import com.paulasantana.common.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public interface ConsumerService<T> {
  void parse(ConsumerRecord<String, Message<T>> record) throws IOException;

  String getTopic();

  String getConsumerGroup();
}
