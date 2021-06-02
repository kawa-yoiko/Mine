package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"github.com/gorilla/mux"
	"net/http"
	"strconv"
)

func postMessageSend(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)

	nickname := r.PostFormValue("to_user")
	contents := r.PostFormValue("contents")

	uOther := models.User{Nickname: nickname}
	if err := uOther.ReadByNickname(); err != nil {
		panic(err)
	}

	m := models.Message{
		ToUser:   uOther,
		FromUser: u,
		Contents: contents,
	}
	if err := m.Create(); err != nil {
		panic(err)
	}

	write(w, 200, jsonPayload{"id": m.Id, "timestamp": m.Timestamp})
}

func getMessageWith(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)

	nickname := mux.Vars(r)["nickname"]
	start, _ := strconv.Atoi(query(r, "start"))
	count, _ := strconv.Atoi(query(r, "count"))

	uOther := models.User{Nickname: nickname}
	if err := uOther.ReadByNickname(); err != nil {
		panic(err)
	}

	messages, err := models.ReadMessagesBetweenUsers(u.Id, uOther.Id, start, count)
	if err != nil {
		panic(err)
	}
	write(w, 200, messages)
}

func init() {
	registerHandler("/message/send", postMessageSend, "POST")
	registerHandler("/message/with/{nickname}", getMessageWith, "GET")
}
