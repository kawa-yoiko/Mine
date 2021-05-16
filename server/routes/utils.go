package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"encoding/json"
	"net/http"
)

func query(r *http.Request, key string) string {
	values := r.URL.Query()[key]
	if values != nil {
		return values[0]
	} else {
		return ""
	}
}

type jsonPayload map[string]interface{}

func write(w http.ResponseWriter, status int, p interface{}) {
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

var JwtSecret []byte
