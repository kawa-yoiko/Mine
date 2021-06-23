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

	// XXX: For debug use: send a system message
	m = models.Message{
		ToUser:   uOther,
		FromUser: models.User{Id: -1},
		Contents: "System message",
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
	if nickname == "n" {
		uOther.Id = -1
	} else if err := uOther.ReadByNickname(); err != nil {
		panic(err)
	}

	messages, err := models.ReadMessagesBetweenUsers(u.Id, uOther.Id, start, count)
	if err != nil {
		panic(err)
	}
	write(w, 200, messages)
}

func getMessageLatest(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)
	messages, err := models.ReadLatestMessages(u.Id)
	if err != nil {
		panic(err)
	}
	write(w, 200, messages)
}

func postMessageRead(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)

	nickname := mux.Vars(r)["nickname"]
	uOther := models.User{Nickname: nickname}
	if nickname == "n" {
		uOther.Id = -1
	} else if err := uOther.ReadByNickname(); err != nil {
		panic(err)
	}

	if err := models.MarkMessagesAsRead(u.Id, uOther.Id); err != nil {
		panic(err)
	}

	write(w, 200, jsonPayload{})
}

func init() {
	registerHandler("/message/send", postMessageSend, "POST")
	registerHandler("/message/with/{nickname}", getMessageWith, "GET")
	registerHandler("/message/latest", getMessageLatest, "GET")
	registerHandler("/message/read/{nickname}", postMessageRead, "POST")
}
