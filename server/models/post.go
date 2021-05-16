package models

import (
	"fmt"
	"strings"
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
	Tags        []string
}

func init() {
	registerSchema("post",
		"id SERIAL PRIMARY KEY",
		"author_id INTEGER NOT NULL",
		"timestamp BIGINT NOT NULL",
		"type INTEGER NOT NULL",
		"is_published BOOLEAN NOT NULL",
		"caption TEXT NOT NULL",
		"contents TEXT NOT NULL",
		"ADD CONSTRAINT author_ref FOREIGN KEY (author_id) REFERENCES mine_user (id)",
	)
	registerSchema("post_tag",
		"post_id INTEGER NOT NULL",
		"tag TEXT NOT NULL",
		"ADD CONSTRAINT post_ref FOREIGN KEY (post_id) REFERENCES post (id)",
	)
	registerSchema("comment",
		"id SERIAL PRIMARY KEY",
		"author_id INTEGER NOT NULL",
		"timestamp BIGINT NOT NULL",
		"reply_to INTEGER NOT NULL",
		"contents TEXT NOT NULL",
		"ADD CONSTRAINT author_ref FOREIGN KEY (author_id) REFERENCES mine_user (id)",
		"ADD CONSTRAINT reply_to_ref FOREIGN KEY (reply_to) REFERENCES comment (id)",
	)
}

func (p *Post) Repr() map[string]interface{} {
	return map[string]interface{}{
		"author":    p.Author.ReprShort(),
		"timestamp": p.Timestamp,
		"type":      p.Type,
		"caption":   p.Caption,
		"contents":  p.Contents,
		"tags":      p.Tags,
	}
}

func (p *Post) Create() error {
	p.Timestamp = time.Now().Unix()
	err := db.QueryRow("INSERT INTO "+
		"post (author_id, timestamp, type, is_published, caption, contents) "+
		"VALUES ($1, $2, $3, $4, $5, $6) RETURNING id",
		p.Author.Id, p.Timestamp, p.Type, p.IsPublished, p.Caption, p.Contents,
	).Scan(&p.Id)
	if err != nil {
		return err
	}

	// Create tags
	if len(p.Tags) == 0 {
		return nil
	}
	placeholders := strings.Builder{}
	values := []interface{}{}
	for i, tag := range p.Tags {
		if i != 0 {
			fmt.Fprintf(&placeholders, ", ")
		}
		fmt.Fprintf(&placeholders, "($%d, $%d)", i*2+1, i*2+2)
		values = append(values, p.Id, tag)
	}
	_, err = db.Exec(
		"INSERT INTO post_tag (post_id, tag) VALUES "+placeholders.String(),
		values...)
	return err
}

func (p *Post) Read() error {
	row := db.QueryRow("SELECT "+
		"post.*, mine_user.nickname, mine_user.avatar "+
		"FROM post INNER JOIN mine_user ON post.author_id = mine_user.id "+
		"WHERE post.id = $1", p.Id)
	err := row.Scan(&p.Id, &p.Author.Id, &p.Timestamp, &p.Type,
		&p.IsPublished, &p.Caption, &p.Contents,
		&p.Author.Nickname, &p.Author.Avatar)
	if err != nil {
		return err
	}

	rows, err := db.Query("SELECT tag FROM post_tag WHERE post_id = $1", p.Id)
	if err != nil {
		return err
	}
	defer rows.Close()
	tags := []string{}
	for rows.Next() {
		var tag string
		if err := rows.Scan(&tag); err != nil {
			return err
		}
		tags = append(tags, tag)
	}
	p.Tags = tags
	return rows.Err()
}
