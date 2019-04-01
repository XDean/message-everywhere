package xdean.msgew.server.service;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import xdean.msgew.server.model.Message;

public interface MessageService {
  void push(Message msg);

  Flowable<Message> fetch();

  Observable<Message> observe();
}
