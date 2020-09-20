package com.paulasantana;

import java.math.BigDecimal;

public class Order {

  private final String orderId;

  private final BigDecimal amount;

  private final String email;


  public Order(String orderId, BigDecimal amount, String email) {
    this.orderId = orderId;
    this.amount = amount;
    this.email = email;
  }

  public boolean isFraud() {
    return this.amount.compareTo(new BigDecimal("4500")) >= 0;
  }

  @Override
  public String toString() {
    return "Order{" +
        "orderId='" + orderId + '\'' +
        ", amount=" + amount +
        ", email='" + email + '\'' +
        '}';
  }

  public String getEmail() {
    return email;
  }

  public String getOrderId() {
    return orderId;
  }
}
