package handler

import (
	"github.com/gorilla/websocket"
	"github.com/labstack/echo/v4"
	"github.com/xdean/goex/xecho"
	"github.com/xdean/message-everywhere/model"
	"github.com/xdean/message-everywhere/service"
	"net/http"
	"strings"
)

var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool {
		return true
	},
}

func SendMessage(c echo.Context) error {
	var msg model.Message
	xecho.MustBind(c, &msg)
	service.Repo.MessageService.Send(msg)
	return c.JSON(http.StatusOK, xecho.M("Send success"))
}

func GetOrObserveMessage(c echo.Context) error {
	if strings.Contains(strings.ToLower(c.Request().Header.Get(echo.HeaderUpgrade)), "websocket") {
		return ObserveMessage(c)
	} else {
		return GetMessage(c)
	}
}

func GetMessage(c echo.Context) error {
	type Param struct {
		Limit  int `json:"limit" form:"limit" query:"limit"`
		Offset int `json:"offset" form:"offset" query:"offset"`
	}
	param := new(Param)
	xecho.MustBindAndValidate(c, param)

	done := make(chan interface{})
	messageStream := make(chan model.Message, 10)
	service.Repo.MessageService.Fetch(service.MessageListener{
		Done:    done,
		Message: messageStream,
	}, param.Offset)

	messages := make([]model.Message, 0)

	for i := 0; i < param.Limit; i++ {
		if msg, ok := <-messageStream; ok {
			messages = append(messages, msg)
		} else {
			break
		}
	}
	close(done)

	return c.JSON(http.StatusOK, messages)
}

func ObserveMessage(c echo.Context) error {
	ws, err := upgrader.Upgrade(c.Response(), c.Request(), nil)
	xecho.MustNoError(err)
	defer ws.Close()

	done := make(chan interface{})
	messageStream := make(chan model.Message, 10)

	service.Repo.MessageService.Observe(service.MessageListener{
		Done:    done,
		Message: messageStream,
	})

	ws.SetCloseHandler(func(code int, text string) error {
		close(done)
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
