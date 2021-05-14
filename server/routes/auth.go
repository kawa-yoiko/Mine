package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"fmt"
	"net/http"
)

// curl http://localhost:2317/signup -i -d "nickname=kayuyuko&email=kyyk@kawa.moe&password=88888"
func postSignup(w http.ResponseWriter, r *http.Request) {
	nickname := r.PostFormValue("nickname")
	email := r.PostFormValue("email")
	password := r.PostFormValue("password")

	u := models.User{}
	u.Nickname = nickname
	u.Email = email
	u.Password = password

	if err := u.Create(); err != nil {
		if err, ok := err.(models.UserCreateError); ok {
			w.WriteHeader(400)
			fmt.Fprintf(w, `{"error": %d}`, err.Code)
			return
		}
		panic(err)
	}
}

func init() {
	registerHandler("/signup", postSignup, "POST")
}
