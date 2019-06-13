package handler

import (
	"github.com/labstack/echo/v4"
	"github.com/xdean/goex/xecho"
	"github.com/xdean/message-everywhere/model"
	"github.com/xdean/message-everywhere/service"
	"net/http"
)

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
	return nil
}
