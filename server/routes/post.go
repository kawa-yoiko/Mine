package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"github.com/gorilla/mux"
	"net/http"
	"strconv"
	"strings"
)

func postPostNew(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)

	ty, err := strconv.Atoi(r.PostFormValue("type"))
	if err != nil || ty < 0 || ty > 3 {
		w.WriteHeader(400)
		return
	}

	c := referredCollection(u, r.PostFormValue("collection"))

	p := models.Post{
		Author:     u,
		Type:       int32(ty),
		Caption:    r.PostFormValue("caption"),
		Contents:   r.PostFormValue("contents"),
		Collection: models.Collection{Id: c.Id},
		Tags:       strings.Split(r.PostFormValue("tags"), ","),
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
		panic(err)
	}

	write(w, 200, p.Repr())
}

func postPostCommentNew(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)

	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	replyTo, _ := strconv.Atoi(r.PostFormValue("reply_to"))
	c := models.Comment{
		Post:     models.Post{Id: int32(id)},
		Author:   u,
		ReplyTo:  int32(replyTo),
		Contents: r.PostFormValue("contents"),
	}
	if err := c.Create(); err != nil {
		panic(err)
	}
	write(w, 200, jsonPayload{"id": c.Id})
}

func getPostComments(w http.ResponseWriter, r *http.Request) {
	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	start, _ := strconv.Atoi(query(r, "start"))
	count, _ := strconv.Atoi(query(r, "count"))
	replyRoot := -1
	if replyRootParsed, err := strconv.Atoi(query(r, "reply_root")); err == nil {
		replyRoot = replyRootParsed
	}

	comments, err := models.ReadComments(id, start, count, replyRoot)
	if err != nil {
		panic(err)
	}
	write(w, 200, comments)
}

func postPostUpvote(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)

	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	isUpvote, err := strconv.Atoi(r.PostFormValue("is_upvote"))
	if err != nil {
		panic(err)
	}

	p := models.Post{Id: int32(id)}
	if err := p.Upvote(u, isUpvote != 0); err != nil {
		panic(err)
	}
	write(w, 200, jsonPayload{"upvote_count": p.UpvoteCount})
}

func postPostStar(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)

	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	isStar, err := strconv.Atoi(r.PostFormValue("is_star"))
	if err != nil {
		panic(models.CheckedError{400})
	}

	p := models.Post{Id: int32(id)}
	if err := p.Star(u, isStar != 0); err != nil {
		panic(err)
	}
	write(w, 200, jsonPayload{"star_count": p.StarCount})
}

func postPostSetCollection(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)

	id, _ := strconv.Atoi(mux.Vars(r)["id"])

	c := referredCollection(u, r.PostFormValue("collection_id"))

	p := models.Post{Id: int32(id)}
	if err := p.Read(); err != nil {
		panic(err)
	}

	if p.Author.Id != u.Id {
		panic(models.CheckedError{403})
	}

	if err := p.SetCollection(c); err != nil {
		panic(err)
	}
	write(w, 200, jsonPayload{})
}

func init() {
	registerHandler("/post/new", postPostNew, "POST")
	registerHandler("/post/{id}", getPost, "GET")
	registerHandler("/post/{id}/comment/new", postPostCommentNew, "POST")
	registerHandler("/post/{id}/comments", getPostComments, "GET")
	registerHandler("/post/{id}/upvote", postPostUpvote, "POST")
	registerHandler("/post/{id}/star", postPostStar, "POST")
	registerHandler("/post/{id}/set_collection", postPostSetCollection, "POST")
}
