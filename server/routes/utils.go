package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"database/sql"
	"encoding/json"
	"errors"
	"net/http"

	"github.com/lib/pq"
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

func handleError(w http.ResponseWriter, err error) {
	status := 500
	if err, ok := err.(models.CheckedError); ok {
		status = err.Status
	}
	if err, ok := err.(*pq.Error); ok {
		if err.Code.Class() == "23" {
			// Integrity Constraint Violation
			status = 400
		} else {
			println(err.Code.Class())
		}
	}
	if errors.Is(err, sql.ErrNoRows) {
		status = 404
	}
	if status == 500 {
		http.Error(w, err.Error(), 500)
	} else {
		w.WriteHeader(status)
	}
}
