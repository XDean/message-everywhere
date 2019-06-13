package handler

import (
	"github.com/gorilla/websocket"
	"github.com/labstack/echo/v4"
	"github.com/xdean/goex/xecho"
	"github.com/xdean/message-everywhere/model"
	"github.com/xdean/message-everywhere/service"
	"net/http"
)

var upgrader = websocket.Upgrader{}

func SendMessage(c echo.Context) error {
	var msg model.Message
	xecho.MustBind(c, &msg)
	service.MessageService.Send(msg)
	return c.JSON(http.StatusOK, xecho.M("Send success"))
}

func GetMessage(c echo.Context) error {
	return nil
}

func ObserveMessage(c echo.Context) error {

	ws, err := upgrader.Upgrade(c.Response(), c.Request(), nil)
	xecho.MustNoError(err)
	defer ws.Close()

	done := make(chan bool)
	messageStream := make(chan model.Message, 10)
	service.MessageService.Observe(func(msg model.Message) bool {
		select {
		case messageStream <- msg:
			return true
		case <-done:
			close(messageStream)
			return false
		}
	})

	ws.SetCloseHandler(func(code int, text string) error {
		done <- true
		return nil
	})

	// read and ignore
	go func() {
		for {
			_, _, err := ws.ReadMessage()
			if err != nil {
				break
			}
		}
	}()

	// write message
	for {
		select {
		case <-done:
			return nil
		case msg := <-messageStream:
			err := ws.WriteJSON(msg)
			if err != nil {
				return err
			}
		}
	}
}
