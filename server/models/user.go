package models

func init() {
	registerSchema("mine_user",
		"id SERIAL PRIMARY KEY",
		"nickname TEXT NOT NULL",
	)
}
