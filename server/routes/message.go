package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"net/http"
)

func postMessageSend(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)

	nickname := r.PostFormValue("to_user")
	contents := r.PostFormValue("contents")

	uRecv := models.User{Nickname: nickname}
	if err := uRecv.ReadByNickname(); err != nil {
		panic(err)
	}

	m := models.Message{
		ToUser:   uRecv,
		FromUser: u,
		Contents: contents,
	}
	if err := m.Create(); err != nil {
		panic(err)
	}

	write(w, 200, jsonPayload{"id": m.Id, "timestamp": m.Timestamp})
}

func init() {
	registerHandler("/message/send", postMessageSend, "POST")
}
