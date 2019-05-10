package xdean.msgew.client.android.model;

import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Messager {
  String provider;
  String id;
  String name;
  Map<String, String> extra;
}
