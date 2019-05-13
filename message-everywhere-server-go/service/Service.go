package service

import "github.com/xdean/message-everywhere/model"

// MessageListener accept Message and return continue or not。 It will be called sync,
// so any long-time work must run the task in goroutinue.
type MessageListener func(model.Message) bool

// IMessageService interface
type IMessageService interface {
	Send(model.Message)
	Fetch(MessageListener)
	Observe(MessageListener)
}
