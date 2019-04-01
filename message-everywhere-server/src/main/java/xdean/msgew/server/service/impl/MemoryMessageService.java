package xdean.msgew.server.service.impl;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

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

  @Override
  public void push(Message msg) {
    messages.addLast(msg);
    messageSubject.onNext(msg);
  }

  @Override
  public Flowable<Message> fetch() {
    return Flowable.fromIterable(() -> messages.descendingIterator());
  }

  @Override
  public Observable<Message> observe() {
    return messageSubject;
  }
}
