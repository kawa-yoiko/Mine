package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

	jwt "github.com/dgrijalva/jwt-go/v4"

	"database/sql"
	"errors"
	"net/http"
	"strings"
)

func postSignup(w http.ResponseWriter, r *http.Request) {
	nickname := r.PostFormValue("nickname")
	email := r.PostFormValue("email")
	password := r.PostFormValue("password")
	avatar := r.PostFormValue("avatar")
	signature := r.PostFormValue("signature")

	u := models.User{}
	u.Nickname = nickname
	u.Email = email
	u.Password = password
	u.Avatar = avatar
	u.Signature = signature

	if err := u.Create(); err != nil {
		if err, ok := err.(models.UserCreateError); ok {
			write(w, 400, jsonPayload{"error": err.Code})
			return
		}
		panic(err)
	}

	write(w, 200, jsonPayload{"error": 0})
}

func postLogin(w http.ResponseWriter, r *http.Request) {
	nickname := r.PostFormValue("nickname")
	password := r.PostFormValue("password")

	u := models.User{}
	u.Nickname = nickname
	if err := u.ReadByNickname(); err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			w.WriteHeader(400)
			return
		}
		panic(err)
	}

	if !u.VerifyPassword(password) {
		w.WriteHeader(400)
		return
	}

	token, _ := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"uid": u.Id,
	}).SignedString(JwtSecret)
	write(w, 200, jsonPayload{"user": u.Repr(), "token": token})
}

func auth(r *http.Request) (models.User, bool) {
	authorization := strings.SplitN(r.Header.Get("Authorization"), " ", 2)
	if len(authorization) != 2 || authorization[0] != "Bearer" {
		return models.User{}, false
	}
	tokenString := authorization[1]
	token, err := jwt.Parse(tokenString,
		func(*jwt.Token) (interface{}, error) { return JwtSecret, nil })
	if err != nil {
		return models.User{}, false
	}
	claims, ok := token.Claims.(jwt.MapClaims)
	if !ok {
		return models.User{}, false
	}
	uid, ok := claims["uid"].(float64)
	if !ok {
		return models.User{}, false
	}

	u := models.User{Id: int32(uid)}
	if err := u.ReadById(); err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return models.User{}, false
		}
		panic(err)
	}

	return u, true
}

func getWhoAmI(w http.ResponseWriter, r *http.Request) {
	u, ok := auth(r)
	if !ok {
		write(w, 400, jsonPayload{})
		return
	}

	write(w, 200, u.Repr())
}

func postWhoAmIEdit(w http.ResponseWriter, r *http.Request) {
	u, ok := auth(r)
	if !ok {
		panic(models.CheckedError{400})
	}

	u.Signature = r.PostFormValue("signature")
	if err := u.Update(); err != nil {
		panic(err)
	}

	write(w, 200, u.Repr())
}

func init() {
	registerHandler("/signup", postSignup, "POST")
	registerHandler("/login", postLogin, "POST")
	registerHandler("/whoami", getWhoAmI, "GET")
	registerHandler("/whoami/edit", postWhoAmIEdit, "POST")
}
