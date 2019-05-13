package model

// Message type
type Message struct {
	From      Messager          `json:"from"`
	To        Messager          `json:"to"`
	Timestamp uint64            `json:"timestamp"`
	Content   string            `json:"content"`
	Extra     map[string]string `json:"extra"`
}

// Messager type
type Messager struct {
	Provider string            `json:"provider"`
	ID       string            `json:"id"`
	Name     string            `json:"name"`
	Extra    map[string]string `json:"extra"`
}
