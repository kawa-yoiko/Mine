package models

import (
	"time"
)

type Message struct {
	Id        int32
	Timestamp int64
	ToUser    User
	FromUser  User
	Contents  string
}

func init() {
	registerSchema("message",
		"id SERIAL PRIMARY KEY",
		"timestamp BIGINT NOT NULL",
		"to_user_id INTEGER NOT NULL",
		"from_user_id INTEGER", // nullable
		"contents TEXT NOT NULL",
		"ADD CONSTRAINT to_user_ref FOREIGN KEY (to_user_id) REFERENCES mine_user (id)",
		"ADD CONSTRAINT from_user_ref FOREIGN KEY (from_user_id) REFERENCES mine_user (id)",
	)
}

func (m *Message) Repr() map[string]interface{} {
	return map[string]interface{}{
		"id":        m.Id,
		"timestamp": m.Timestamp,
		"to_user":   m.ToUser.ReprBrief(),
		"from_user": m.FromUser.ReprBrief(),
		"contents":  m.Contents,
	}
}

func (m *Message) Create() error {
	m.Timestamp = time.Now().Unix()
	err := db.QueryRow(`INSERT INTO
		message (timestamp, to_user_id, from_user_id, contents)
		VALUES ($1, $2, $3, $4) RETURNING id`,
		m.Timestamp, m.ToUser.Id, m.FromUser.Id, m.Contents,
	).Scan(&m.Id)
	return err
}
