package com.paulasantana.consumer;

import java.sql.SQLException;

public interface ServiceFactory<T> {
  ConsumerService<T> create() throws Exception;
}
