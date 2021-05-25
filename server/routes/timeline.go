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

func init() {
	registerHandler("/subscription_timeline", getSubscriptionTimeline, "GET")
	registerHandler("/discover_timeline", getDiscoverTimeline, "GET")
}
