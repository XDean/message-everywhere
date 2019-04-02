package xdean.msgew.server.model;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = Messager.MessagerBuilder.class)
public class Messager {
  String provider;
  String id;
  String name;
  Map<String, String> extra;

  @JsonPOJOBuilder(withPrefix = "")
  public static class MessagerBuilder {

  }
}
