package com.paulasantana.producer;

import com.paulasantana.common.CorreleationId;
import com.paulasantana.common.Message;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, Message<T>> producer;

    public KafkaDispatcher(){
        this.producer = new KafkaProducer<String, Message<T>>(properties());
    }

    private static Properties properties() {
        var properties = new Properties();

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG,"all");

        return properties;
    }

    public void send(String topic, String key, T payload, CorreleationId correlationId) throws ExecutionException, InterruptedException {
        Future<RecordMetadata> future = sendAsync(topic, key, payload, correlationId);
        future.get();
    }

    private Future<RecordMetadata> sendAsync(String topic, String key, T payload, CorreleationId correlationId) {
        var value = new Message<T>(correlationId.continueWith("_"+topic), payload);
        var record = new ProducerRecord<>( topic, key, value);
        Callback callback = (data, ex) -> {

            if (ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println("Topic - " + data.topic() + " / Partition - " + data.partition() + " / Offset - " + data.offset() + " / Timestamp - " + data.timestamp());
        };
        return producer.send(record, callback);
    }


    @Override
    public void close()  {
        producer.close();
    }
}
