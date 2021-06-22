package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"net/http"
	"strconv"
)

func getSubscriptionTimeline(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)
	start, _ := strconv.Atoi(query(r, "start"))
	count, _ := strconv.Atoi(query(r, "count"))

	posts, err := models.SubscriptionTimeline(u.Id, start, count)
	if err != nil {
		panic(err)
	}
	write(w, 200, posts)
}

func getDiscoverTimeline(w http.ResponseWriter, r *http.Request) {
	u, _ := auth(r)

	start, _ := strconv.Atoi(query(r, "start"))
	count, _ := strconv.Atoi(query(r, "count"))

	posts, err := models.DiscoverTimeline(u.Id, start, count)
	if err != nil {
		panic(err)
	}
	write(w, 200, posts)
}

func getStarTimeline(w http.ResponseWriter, r *http.Request) {
	u := mustAuth(r)
	start, _ := strconv.Atoi(query(r, "start"))
	count, _ := strconv.Atoi(query(r, "count"))

	posts, err := models.StarTimeline(u.Id, start, count)
	if err != nil {
		panic(err)
	}
	write(w, 200, posts)
}

func getSearchTags(w http.ResponseWriter, r *http.Request) {
	tag := query(r, "tag")
	if tag == "" {
		panic(models.CheckedError{400})
	}
	tags, err := models.SearchTags(tag)
	if err != nil {
		panic(err)
	}
	write(w, 200, tags)
}

func getSearchPosts(w http.ResponseWriter, r *http.Request) {
	u, _ := auth(r)

	tag := query(r, "tag")
	start, _ := strconv.Atoi(query(r, "start"))
	count, _ := strconv.Atoi(query(r, "count"))
	ty := query(r, "type")

	posts, err := models.SearchPostsByTag(u.Id, tag, start, count, ty)
	if err != nil {
		panic(err)
	}
	write(w, 200, posts)
}

func getSearchCollections(w http.ResponseWriter, r *http.Request) {
	u, _ := auth(r)

	tag := query(r, "tag")
	start, _ := strconv.Atoi(query(r, "start"))
	count, _ := strconv.Atoi(query(r, "count"))

	collections, err := models.SearchCollectionsByTag(u.Id, tag, start, count)
	if err != nil {
		panic(err)
	}
	write(w, 200, collections)
}

func init() {
	registerHandler("/subscription_timeline", getSubscriptionTimeline, "GET")
	registerHandler("/discover_timeline", getDiscoverTimeline, "GET")
	registerHandler("/star_timeline", getStarTimeline, "GET")
	registerHandler("/search_tags", getSearchTags, "GET")
	registerHandler("/search_posts", getSearchPosts, "GET")
	registerHandler("/search_collections", getSearchCollections, "GET")
}
