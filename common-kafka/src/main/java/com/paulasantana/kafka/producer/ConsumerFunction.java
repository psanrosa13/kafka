package com.paulasantana.kafka.producer;

import com.paulasantana.kafka.common.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerFunction<T> {
    void consume(ConsumerRecord<String , Message<T>> record) throws Exception;
}
