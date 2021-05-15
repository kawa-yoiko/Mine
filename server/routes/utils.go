package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"encoding/json"
	"net/http"
)

type jsonPayload map[string]interface{}

func write(w http.ResponseWriter, status int, p jsonPayload) {
	w.WriteHeader(status)
	json.NewEncoder(w).Encode(p)
}

func EnableResetEndpoint() {
	registerHandler("/reset", func(w http.ResponseWriter, r *http.Request) {
		if err := models.ResetDatabase(); err != nil {
			panic(err)
		}
	}, "POST")
}
