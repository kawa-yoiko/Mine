package models

import (
	"github.com/lib/pq"

	"fmt"
	"golang.org/x/crypto/bcrypt"
	"regexp"
)

type User struct {
	Id        int32
	Nickname  string
	Email     string
	Password  string
	Avatar    string
	Signature string
}

func init() {
	registerSchema("mine_user",
		"id SERIAL PRIMARY KEY",
		"nickname TEXT NOT NULL",
		"email TEXT NOT NULL",
		"password TEXT NOT NULL",
		"avatar TEXT NOT NULL",
		"signature TEXT NOT NULL",
		"ADD CONSTRAINT nickname_unique UNIQUE (nickname)",
		"ADD CONSTRAINT email_unique UNIQUE (email)",
	)
}

func (u *User) Repr() map[string]interface{} {
	return map[string]interface{}{
		"nickname":  u.Nickname,
		"avatar":    u.Avatar,
		"signature": u.Signature,
	}
}

func (u *User) hashPassword() {
	hashed, err := bcrypt.GenerateFromPassword([]byte(u.Password), bcrypt.DefaultCost)
	if err != nil {
		panic(err)
	}
	u.Password = string(hashed)
}

type UserCreateError struct{ Code uint }

const (
	UserCreateErrorNone = iota
	UserCreateErrorFormat
	UserCreateErrorNicknameExists
	UserCreateErrorEmailExists
)

func (e UserCreateError) Error() string {
	return fmt.Sprintf("UserCreateError: %d", e.Code)
}

// RFC 5322
var rxEmail = regexp.MustCompile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
var rxPassword = regexp.MustCompile("[\x00-\x7F]{6,32}")

func (u *User) Create() error {
	if !(len([]rune(u.Nickname)) >= 3 && len([]rune(u.Nickname)) <= 16 &&
		rxEmail.MatchString(u.Email) &&
		rxPassword.MatchString(u.Password)) {
		return UserCreateError{UserCreateErrorFormat}
	}

	u.hashPassword()
	err := db.QueryRow("INSERT INTO "+
		"mine_user (nickname, email, password, avatar, signature) "+
		"VALUES ($1, $2, $3, $4, $5) RETURNING id",
		u.Nickname, u.Email, u.Password, u.Avatar, u.Signature,
	).Scan(&u.Id)
	if err, ok := err.(*pq.Error); ok {
		if err.Code == "23505" { // unique_violation
			if err.Constraint == "nickname_unique" {
				return UserCreateError{UserCreateErrorNicknameExists}
			} else if err.Constraint == "email_unique" {
				return UserCreateError{UserCreateErrorEmailExists}
			}
		}
	}
	return err
}

func (u *User) read(field string, value interface{}) error {
	row := db.QueryRow("SELECT id, nickname, email, password, avatar, signature "+
		"FROM mine_user WHERE "+field+" = $1", value)
	err := row.Scan(&u.Id, &u.Nickname, &u.Email, &u.Password, &u.Avatar, &u.Signature)
	return err
}

func (u *User) ReadById() error {
	return u.read("id", u.Id)
}

func (u *User) ReadByNickname() error {
	return u.read("nickname", u.Nickname)
}

func (u *User) ReadByEmail() error {
	return u.read("email", u.Email)
}

func (u *User) Update() error {
	_, err := db.Exec("UPDATE mine_user SET "+
		"nickname = $1, email = $2, avatar = $3, signature = $4 "+
		"WHERE id = $5",
		u.Nickname, u.Email, u.Avatar, u.Signature,
		u.Id,
	)
	return err
}

func (u *User) UpdatePassword() error {
	u.hashPassword()
	_, err := db.Exec("UPDATE mine_user SET password = $1 WHERE id = $2", u.Password, u.Id)
	return err
}

func (u *User) VerifyPassword(pw string) bool {
	err := bcrypt.CompareHashAndPassword([]byte(u.Password), []byte(pw))
	return err == nil
}
