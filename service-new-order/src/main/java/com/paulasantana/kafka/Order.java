package com.paulasantana.kafka;

import java.math.BigDecimal;

public class Order {

    private final String uderId;

    private final String orderId;

    private final BigDecimal amount;

    public Order(String uderId, String orderId, BigDecimal amount) {
        this.uderId = uderId;
        this.orderId = orderId;
        this.amount = amount;
    }
}
