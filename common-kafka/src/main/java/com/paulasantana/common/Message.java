package com.paulasantana.common;

public class Message<T> {

    private final CorreleationId id;
    private final T payload;

    public Message(CorreleationId id, T payload) {
        this.id = id;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", payload=" + payload +
                '}';
    }


    public Object getPayload() {
        return payload;
    }

    public CorreleationId getId() {
        return id;
    }
}
