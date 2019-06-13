package main

import (
	"github.com/labstack/echo/v4/middleware"
	"github.com/xdean/goex/xecho"
	"github.com/xdean/message-everywhere/handler"
	"net/http"

	"github.com/labstack/echo/v4"
)

func main() {
	e := echo.New()

	e.Validator = xecho.NewValidator()

	e.Use(middleware.Logger())
	e.Use(middleware.Recover())
	e.Use(xecho.BreakErrorRecover())

	e.GET("/hello", func(context echo.Context) error {
		return context.JSON(http.StatusOK, "hello "+context.Request().RemoteAddr)
	})

	apiGroup := e.Group("/api")
	apiGroup.POST("/message", handler.SendMessage)
	apiGroup.GET("/message", handler.GetOrObserveMessage)

	e.Logger.Fatal(e.Start(":11070"))
}
