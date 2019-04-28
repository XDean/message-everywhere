package xdean.msgew.server.model;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = Message.MessageBuilder.class)
public class Message {
  Messager from;
  Messager to;
  long timestamp;
  String content;
  Map<String, String> extra;

  @JsonPOJOBuilder(withPrefix = "")
  public static class MessageBuilder {

  }
}
