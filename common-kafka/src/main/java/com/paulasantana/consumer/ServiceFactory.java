package com.paulasantana.consumer;

public interface ServiceFactory<T> {
  ConsumerService<T> create();
}
