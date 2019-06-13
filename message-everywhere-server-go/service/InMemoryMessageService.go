package service

import (
	"github.com/xdean/message-everywhere/model"
)

// InMemMessageService is a in memory implementation of IMessageService
type InMemMessageService struct {
	messages    []model.Message
	listeners   []MessageListener
	commandChan chan interface{}
}

// Send message
type Send struct {
	message model.Message
}

// Fetch message
type Fetch struct {
	listener MessageListener
	offset   int
}

// Observe message
type Observe struct {
	listener MessageListener
	add      bool
}

// NewInMemMessageService create a new message service
func NewInMemMessageService() *InMemMessageService {
	s := InMemMessageService{
		messages:    make([]model.Message, 0),
		listeners:   make([]MessageListener, 0),
		commandChan: make(chan interface{}, 10),
	}
	go func() {
		for {
			c := <-s.commandChan
			switch cmd := c.(type) {
			case Send:
				s.messages = append(s.messages, cmd.message)
				for _, listener := range s.listeners {
					go func(ml MessageListener, msg model.Message) {
						select {
						case <-ml.Done:
							close(ml.Message)
							s.commandChan <- Observe{listener: ml, add: false}
						case ml.Message <- msg:
						}
					}(listener, cmd.message)
				}
			case Fetch:
				if cmd.offset > len(s.messages) {
					close(cmd.listener.Message)
					return
				}
				go func(ml MessageListener, msgs []model.Message) {
					for _, msg := range msgs {
						select {
						case <-ml.Done:
							return
						case ml.Message <- msg:
						}
					}
					close(ml.Message)
				}(cmd.listener, s.messages[cmd.offset:])
			case Observe:
				if cmd.add {
					s.listeners = append(s.listeners, cmd.listener)
				} else {
					var index int
					for i, l := range s.listeners {
						if l == cmd.listener {
							index = i
							break
						}
					}
					s.listeners = append(s.listeners[:index], s.listeners[index:]...)
				}
			}
		}
	}()
	return &s
}

// Send message to InMemMessageService
func (s *InMemMessageService) Send(msg model.Message) {
	s.commandChan <- Send{msg}
}

// Fetch message
func (s *InMemMessageService) Fetch(l MessageListener, offset int) {
	s.commandChan <- Fetch{listener: l, offset: offset}
}

// Observe message
func (s *InMemMessageService) Observe(l MessageListener) {
	s.commandChan <- Observe{listener: l, add: true}
}
