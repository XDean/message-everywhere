package xdean.msgew.client.android.model;

import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Message {
    Messager from;
    Messager to;
    long timestamp;
    String content;
    Map<String, String> extra;
}
