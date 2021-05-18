package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"github.com/gorilla/mux"
	"net/http"
	"strconv"
	"strings"
)

func postCollectionNew(w http.ResponseWriter, r *http.Request) {
	u, ok := auth(r)
	if !ok {
		w.WriteHeader(401)
		return
	}

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
	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	c := models.Collection{Id: int32(id)}
	if err := c.Read(); err != nil {
		panic(err)
	}

	write(w, 200, c.Repr())
}

func referredCollection(u models.User, r *http.Request) models.Collection {
	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	c := models.Collection{Id: int32(id)}
	if err := c.Read(); err != nil {
		panic(err)
	}
	if c.Author.Id != u.Id {
		panic(models.CheckedError{403})
	}
	return c
}

func postCollectionEditPosts(w http.ResponseWriter, r *http.Request) {
	u, ok := auth(r)
	if !ok {
		w.WriteHeader(401)
		return
	}

	c := referredCollection(u, r)

	op := r.PostFormValue("op")
	if len(op) <= 1 || (op[0] != '+' && op[0] != '-') {
		panic(models.CheckedError{400})
	}
	postId, err := strconv.Atoi(op[1:])
	if err != nil {
		panic(err)
	}

	if err := c.EditPosts(op[0] == '+', int32(postId)); err != nil {
		panic(err)
	}

	write(w, 200, c.Repr())
}

func init() {
	registerHandler("/collection/new", postCollectionNew, "POST")
	registerHandler("/collection/{id}", getCollection, "GET")
	registerHandler("/collection/{id}/edit_posts", postCollectionEditPosts, "POST")
}
