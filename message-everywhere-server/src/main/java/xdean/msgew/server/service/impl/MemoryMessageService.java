package xdean.msgew.server.service.impl;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.inject.Inject;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import xdean.msgew.server.model.Message;
import xdean.msgew.server.service.MessageService;

@Service
public class MemoryMessageService implements MessageService {

  Subject<Message> messageSubject = PublishSubject.create();
  Deque<Message> messages = new ConcurrentLinkedDeque<>();

  KafkaTemplate<?, Message> kafkaTemplate;

  @Override
  public void push(Message msg) {
    // messages.addLast(actual);
    // messageSubject.onNext(actual);
    kafkaTemplate.sendDefault(msg);
  }

  @Override
  public Flowable<Message> fetch() {
    return Flowable.fromIterable(() -> messages.descendingIterator());
  }

  @Override
  public Observable<Message> observe() {
    return messageSubject;
  }

  @KafkaListener(topics = "message")
  public void processMessage(@Payload Message msg) {
    messages.addLast(msg);
    messageSubject.onNext(msg);
  }

  @Inject
  public void setKafkaTemplate(KafkaTemplate<?, Message> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
    kafkaTemplate.setDefaultTopic("message");
  }
}
