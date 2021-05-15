package routes

import (
	"encoding/json"
	"net/http"
)

type jsonPayload map[string]interface{}

func write(w http.ResponseWriter, status int, p jsonPayload) {
	w.WriteHeader(status)
	json.NewEncoder(w).Encode(p)
}
