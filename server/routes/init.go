package routes

import (
	"github.com/gorilla/mux"
	"net/http"
)

var router *mux.Router

func registerHandler(path string, fn func(http.ResponseWriter, *http.Request), methods ...string) {
	if router == nil {
		router = mux.NewRouter()
	}
	if len(methods) == 0 {
		methods = []string{"GET"}
	}
	router.HandleFunc(path, fn).Methods(methods...)
}

func GetRootRouterFunc() func(w http.ResponseWriter, req *http.Request) {
	return func(w http.ResponseWriter, req *http.Request) {
		defer func() {
			if e := recover(); e != nil {
				if e, ok := e.(error); ok {
					handleError(w, e)
				}
			}
		}()
		router.ServeHTTP(w, req)
	}
}
