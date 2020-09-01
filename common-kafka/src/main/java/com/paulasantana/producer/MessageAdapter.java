package com.paulasantana.producer;

import com.google.gson.*;
import com.paulasantana.common.CorreleationId;
import com.paulasantana.common.Message;

import java.lang.reflect.Type;

public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {

  @Override
  public JsonElement serialize(Message message, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    object.addProperty("type", message.getPayload().getClass().getName());
    object.add("payload", context.serialize(message.getPayload()));
    object.add("correlationId", context.serialize(message.getId()));
    return object;
  }

  @Override
  public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    var object = json.getAsJsonObject();
    System.out.println(object);
    System.out.println("aqui-----------------------------------------------------------");
    var payloadType = object.get("type").getAsString();
    var correlationId = (CorreleationId) context.deserialize(object.get("correlationId"), CorreleationId.class);
    try {
      System.out.println("aqui2-----------------------------------------------------------");
      System.out.println("classeeeeeeee" + payloadType);


      var payload = context.deserialize(object.get("payload"), Class.forName(payloadType));
      return new Message(correlationId, payload);
    } catch (ClassNotFoundException e) {
      throw new JsonParseException(e);
    }
  }
}
