package main

import (
	"io"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/xdean/message-everywhere/model"
	"github.com/xdean/message-everywhere/service"
)

func main() {
	router := gin.Default()
	router.GET("/hello", func(c *gin.Context) {
		c.String(http.StatusOK, "Hello World")
	})
	router.POST("/send", func(c *gin.Context) {
		var msg model.Message
		if err := c.ShouldBindJSON(&msg); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		service.MessageService.Send(msg)
		c.Status(http.StatusOK)
	})
	router.GET("/observe", func(c *gin.Context) {
		closeChan := make(chan bool)
		chanStream := make(chan model.Message, 10)
		service.MessageService.Observe(func(msg model.Message) bool {
			select {
			case chanStream <- msg:
				return true
			case <-closeChan:
				close(chanStream)
				return false
			}
		})
		if c.Stream(func(w io.Writer) bool {
			if msg, ok := <-chanStream; ok {
				c.SSEvent("message", msg)
				return true
			}
			return false
		}) {
			closeChan <- true
		}
	})
	router.Run() // listen and serve on 0.0.0.0:8080
}
