package xdean.msgew.server.model;

import java.util.Map;

import lombok.Builder;

import lombok.Value;

@Value
@Builder
public class Message {
  Messager from;
  Messager to;
  int id;
  long timestamp;
  String content;
  Map<String, String> extra;
}
