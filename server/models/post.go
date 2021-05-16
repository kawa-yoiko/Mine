package models

import (
	"time"
)

type Post struct {
	Id          int32
	Author      User
	Timestamp   int64
	Type        int32
	IsPublished bool
	Caption     string
	Contents    string
}

func init() {
	registerSchema("post",
		"id SERIAL PRIMARY KEY",
		"author INTEGER NOT NULL",
		"timestamp BIGINT NOT NULL",
		"type INTEGER NOT NULL",
		"is_published BOOLEAN NOT NULL",
		"caption TEXT NOT NULL",
		"contents TEXT NOT NULL",
		"ADD CONSTRAINT author_ref FOREIGN KEY (author) REFERENCES mine_user (id)",
	)
}

func (p *Post) Repr() map[string]interface{} {
	return map[string]interface{}{
		"author":    p.Author.ReprShort(),
		"timestamp": p.Timestamp,
		"type":      p.Type,
		"caption":   p.Caption,
		"contents":  p.Contents,
		"tags":      []string{},
	}
}

func (p *Post) Create() error {
	p.Timestamp = time.Now().Unix()
	err := db.QueryRow("INSERT INTO "+
		"post (author, timestamp, type, is_published, caption, contents) "+
		"VALUES ($1, $2, $3, $4, $5, $6) RETURNING id",
		p.Author.Id, p.Timestamp, p.Type, p.IsPublished, p.Caption, p.Contents,
	).Scan(&p.Id)
	return err
}

func (p *Post) Read() error {
	row := db.QueryRow("SELECT "+
		"post.*, mine_user.nickname, mine_user.avatar "+
		"FROM post INNER JOIN mine_user ON post.author = mine_user.id "+
		"WHERE post.id = $1", p.Id)
	err := row.Scan(&p.Id, &p.Author.Id, &p.Timestamp, &p.Type,
		&p.IsPublished, &p.Caption, &p.Contents,
		&p.Author.Nickname, &p.Author.Avatar)
	return err
}
