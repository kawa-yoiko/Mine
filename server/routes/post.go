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

	// Notify all subscribers
	ids, err := c.ReadAllSubscribersIds()
	if err != nil {
		panic(err)
	}
	println(len(ids))
	for _, id := range ids {
		if err := models.SendSystemMessage(id, "collection_update " + c.Cover + " " + c.Title); err != nil {
			panic(err)
		}
	}

	write(w, 200, jsonPayload{"id": p.Id})
}

func getPost(w http.ResponseWriter, r *http.Request) {
	u, _ := auth(r)

	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	p := models.Post{Id: int32(id)}
	if err := p.Read(u.Id); err != nil {
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

	if replyTo == -1 {
		p := models.Post{Id: int32(id)}
		if err := p.ReadAuthorId(); err != nil {
			panic(err)
		}
		if err := models.SendSystemMessage(p.Author.Id, "post_comment " + u.Nickname); err != nil {
			panic(err)
		}
	} else {
		cReplied := models.Comment{Id: int32(replyTo)}
		if err := cReplied.ReadAuthorId(); err != nil {
			panic(err)
		}
		if err := models.SendSystemMessage(cReplied.Author.Id, "comment_reply " + u.Nickname); err != nil {
			panic(err)
		}
	}

	write(w, 200, jsonPayload{"id": c.Id})
}

func getPostComments(w http.ResponseWriter, r *http.Request) {
	u, _ := auth(r)

	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	start, _ := strconv.Atoi(query(r, "start"))
	count, _ := strconv.Atoi(query(r, "count"))
	replyRoot := -1
	if replyRootParsed, err := strconv.Atoi(query(r, "reply_root")); err == nil {
		replyRoot = replyRootParsed
	}

	comments, err := models.ReadComments(int32(id), false, start, count, int32(replyRoot), u.Id)
	if err != nil {
		panic(err)
	}
	write(w, 200, comments)
}

func getPostCommentsHot(w http.ResponseWriter, r *http.Request) {
	u, _ := auth(r)
	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	comments, err := models.ReadComments(int32(id), true, 0, 0, -1, u.Id)
	if err != nil {
		panic(err)
	}
	write(w, 200, comments)
}

func postPostUpvote(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)

	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	isUpvote, err := strconv.Atoi(r.PostFormValue("is_upvote"))
	if err != nil || (isUpvote != 0 && isUpvote != 1) {
		panic(models.CheckedError{400})
	}

	p := models.Post{Id: int32(id)}
	if err := p.Upvote(u, isUpvote != 0); err != nil {
		panic(err)
	}

	if err := p.ReadAuthorId(); err != nil {
		panic(err)
	}
	if err := models.SendSystemMessage(p.Author.Id, "post_upvote " + u.Nickname); err != nil {
		panic(err)
	}

	write(w, 200, jsonPayload{"upvote_count": p.UpvoteCount})
}

func postPostStar(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)

	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	isStar, err := strconv.Atoi(r.PostFormValue("is_star"))
	if err != nil || (isStar != 0 && isStar != 1) {
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
	if err := p.Read(u.Id); err != nil {
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

func postPostCommentUpvote(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)

	id, _ := strconv.Atoi(mux.Vars(r)["id"])
	cid, _ := strconv.Atoi(mux.Vars(r)["cid"])
	isUpvote, err := strconv.Atoi(r.PostFormValue("is_upvote"))
	if err != nil || (isUpvote != 0 && isUpvote != 1) {
		panic(models.CheckedError{400})
	}

	c := models.Comment{Id: int32(cid)}
	if err := c.Read(u.Id); err != nil {
		panic(err)
	}
	if c.Post.Id != int32(id) {
		panic(models.CheckedError{400})
	}
	if err := c.Upvote(u, isUpvote != 0); err != nil {
		panic(err)
	}
	write(w, 200, jsonPayload{"upvote_count": c.UpvoteCount})
}

func init() {
	registerHandler("/post/new", postPostNew, "POST")
	registerHandler("/post/{id}", getPost, "GET")
	registerHandler("/post/{id}/comment/new", postPostCommentNew, "POST")
	registerHandler("/post/{id}/comments", getPostComments, "GET")
	registerHandler("/post/{id}/comments/hot", getPostCommentsHot, "GET")
	registerHandler("/post/{id}/upvote", postPostUpvote, "POST")
	registerHandler("/post/{id}/star", postPostStar, "POST")
	registerHandler("/post/{id}/set_collection", postPostSetCollection, "POST")
	registerHandler("/post/{id}/comment/{cid}/upvote", postPostCommentUpvote, "POST")
}
