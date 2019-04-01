package xdean.msgew.server.endpoint;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import xdean.msgew.server.model.Message;
import xdean.msgew.server.service.MessageService;

@RestController
public class MessageEndpoint {
  @Inject
  MessageService service;

  @PostMapping("/send")
  public void send(@RequestBody Message msg) {
    service.push(msg);
  }

  @PostMapping("/fetch")
  public List<Message> fetch(@RequestParam("limit") int limit, @RequestParam("offset") int offset) {
    return service.fetch().skip(offset).limit(limit).toList().blockingGet();
  }
}
