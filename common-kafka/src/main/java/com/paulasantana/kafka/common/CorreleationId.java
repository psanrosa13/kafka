package com.paulasantana.kafka.common;

import java.util.UUID;

public class CorreleationId {

    private final String id;

    public CorreleationId() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "CorreleationId{" +
                "id='" + id + '\'' +
                '}';
    }
}
