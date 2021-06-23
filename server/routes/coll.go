package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"github.com/gorilla/mux"
	"net/http"
	"strconv"
	"strings"
)

func postCollectionNew(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)

	c := models.Collection{
		Author:      u,
		Title:       r.PostFormValue("title"),
		Description: r.PostFormValue("description"),
		Tags:        strings.Split(r.PostFormValue("tags"), ","),
	}
	if err := c.Create(); err != nil {
		panic(err)
	}

	write(w, 200, jsonPayload{"id": c.Id})
}

func getCollection(w http.ResponseWriter, r *http.Request) {
	u, _ := auth(r)

	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	c := models.Collection{Id: int32(id)}
	if err := c.Read(u.Id); err != nil {
		panic(err)
	}

	write(w, 200, c.Repr())
}

func referredCollection(u models.User, idStr string) models.Collection {
	id, err := strconv.Atoi(idStr)
	if err != nil {
		panic(models.CheckedError{400})
	}
	c := models.Collection{Id: int32(id)}
	if err := c.Read(u.Id); err != nil {
		panic(err)
	}
	if c.Author.Id != u.Id {
		panic(models.CheckedError{403})
	}
	return c
}

func postCollectionSubscribe(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)

	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	isSubscribe, err := strconv.Atoi(r.PostFormValue("is_subscribe"))
	if err != nil || (isSubscribe != 0 && isSubscribe != 1) {
		panic(models.CheckedError{400})
	}

	c := models.Collection{Id: int32(id)}
	if err := c.Subscribe(u, isSubscribe != 0); err != nil {
		panic(err)
	}
	write(w, 200, jsonPayload{"subscription_count": c.SubCount})
}

func init() {
	registerHandler("/collection/new", postCollectionNew, "POST")
	registerHandler("/collection/{id}", getCollection, "GET")
	registerHandler("/collection/{id}/subscribe", postCollectionSubscribe, "POST")
}
