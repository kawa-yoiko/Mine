package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

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
			write(w, 400, jsonPayload{"error": err.Code})
			return
		}
		panic(err)
	}

	write(w, 200, jsonPayload{"error": 0})
}

func init() {
	registerHandler("/signup", postSignup, "POST")
}
