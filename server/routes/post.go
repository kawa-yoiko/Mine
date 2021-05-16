package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"database/sql"
	"errors"
	"github.com/gorilla/mux"
	"net/http"
	"strconv"
	"strings"
)

func postPostNew(w http.ResponseWriter, r *http.Request) {
	u, ok := auth(r)
	if !ok {
		w.WriteHeader(401)
		return
	}

	ty, err := strconv.Atoi(r.PostFormValue("type"))
	if err != nil || ty < 0 || ty > 3 {
		w.WriteHeader(400)
		return
	}
	publish, err := strconv.Atoi(r.PostFormValue("publish"))
	if err != nil || publish < 0 || publish > 1 {
		w.WriteHeader(400)
		return
	}

	p := models.Post{
		Author:      u,
		Type:        int32(ty),
		Caption:     r.PostFormValue("caption"),
		Contents:    r.PostFormValue("contents"),
		IsPublished: (publish != 0),
		Tags:        strings.Split(r.PostFormValue("tags"), ","),
	}
	if err := p.Create(); err != nil {
		panic(err)
	}

	write(w, 200, jsonPayload{"id": p.Id})
}

func getPost(w http.ResponseWriter, r *http.Request) {
	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	p := models.Post{Id: int32(id)}
	if err := p.Read(); err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			w.WriteHeader(404)
			return
		}
		panic(err)
	}

	write(w, 200, p.Repr())
}

func init() {
	registerHandler("/post/new", postPostNew, "POST")
	registerHandler("/post/{id}", getPost, "GET")
}
