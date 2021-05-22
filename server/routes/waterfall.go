package routes

import (
	"net/http"
)

func getSubscriptionTimeline(w http.ResponseWriter, r *http.Request) {
}

func init() {
	registerHandler("/subscription_timeline", getSubscriptionTimeline, "GET")
}
