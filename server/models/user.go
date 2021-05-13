package models

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
	)
}

func (u *User) Create() error {
	err := db.QueryRow("INSERT INTO "+
		"mine_user (nickname, email, password, avatar, signature) "+
		"VALUES ($1, $2, $3, $4, $5) RETURNING id",
		u.Nickname,
		u.Email,
		u.Password,
		u.Avatar,
		u.Signature,
	).Scan(&u.Id)
	return err
}
