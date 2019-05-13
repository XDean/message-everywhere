package service

import "github.com/xdean/message-everywhere/model"

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
}

// Observe message
type Observe struct {
	listener MessageListener
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
				listeners := make([]MessageListener, 0)
				for _, l := range s.listeners {
					if l(cmd.message) {
						listeners = append(listeners, l)
					}
				}
				s.listeners = listeners
			case Fetch:
				msgs := s.messages
				for _, msg := range msgs {
					if !cmd.listener(msg) {
						break
					}
				}
			case Observe:
				s.listeners = append(s.listeners, cmd.listener)
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
func (s *InMemMessageService) Fetch(l MessageListener) {
	s.commandChan <- Fetch{l}
}

// Observe message
func (s *InMemMessageService) Observe(l MessageListener) {
	s.commandChan <- Observe{l}
}
