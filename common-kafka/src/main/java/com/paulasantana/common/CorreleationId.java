package com.paulasantana.common;

import java.util.UUID;

public class CorreleationId {

    private final String id;

    public CorreleationId(String title) {
        this.id = title + "(" + UUID.randomUUID().toString() + ")";
    }

    @Override
    public String toString() {
        return "CorreleationId{" +
                "id='" + id + '\'' +
                '}';
    }

    public CorreleationId continueWith(String title) {
        return new CorreleationId(id+"-"+title);
    }
}
