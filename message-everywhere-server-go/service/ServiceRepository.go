package service

// MessageService instance
var Repo = struct {
	MessageService IMessageService
}{
	MessageService: NewInMemMessageService(),
}
