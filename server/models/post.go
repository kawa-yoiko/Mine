package models

import (
	"fmt"
	"strings"
	"time"

	"github.com/lib/pq"
)

type Post struct {
	Id           int32
	Author       User
	Timestamp    int64
	Type         int32
	IsPublished  bool
	Caption      string
	Contents     string
	Tags         []string
	UpvoteCount  int32
	CommentCount int32
	MarkCount    int32
}

type Comment struct {
	Id        int32
	Post      Post
	Author    User
	Timestamp int64
	ReplyTo   int32
	ReplyRoot int32
	Contents  string
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
	registerSchema("post_upvote",
		"post_id INTEGER NOT NULL",
		"user_id INTEGER NOT NULL",
		"ADD CONSTRAINT post_ref FOREIGN KEY (post_id) REFERENCES post (id)",
		"ADD CONSTRAINT user_ref FOREIGN KEY (user_id) REFERENCES mine_user (id)",
		"ADD CONSTRAINT uniq UNIQUE (post_id, user_id)",
	)
	registerSchema("comment",
		"id SERIAL PRIMARY KEY",
		"post_id INTEGER NOT NULL",
		"author_id INTEGER NOT NULL",
		"timestamp BIGINT NOT NULL",
		"reply_to INTEGER",   // nullable
		"reply_root INTEGER", // nullable
		"contents TEXT NOT NULL",
		"ADD CONSTRAINT post_ref FOREIGN KEY (post_id) REFERENCES post (id)",
		"ADD CONSTRAINT author_ref FOREIGN KEY (author_id) REFERENCES mine_user (id)",
		"ADD CONSTRAINT reply_to_ref FOREIGN KEY (reply_to) REFERENCES comment (id)",
		"ADD CONSTRAINT reply_root_ref FOREIGN KEY (reply_root) REFERENCES comment (id)",
	)
}

func (p *Post) Repr() map[string]interface{} {
	return map[string]interface{}{
		"author":        p.Author.ReprShort(),
		"timestamp":     p.Timestamp,
		"type":          p.Type,
		"caption":       p.Caption,
		"contents":      p.Contents,
		"tags":          p.Tags,
		"upvote_count":  p.UpvoteCount,
		"comment_count": p.CommentCount,
		"mark_count":    p.MarkCount,
	}
}

func (c *Comment) Repr() map[string]interface{} {
	return map[string]interface{}{
		"id":        c.Id,
		"author":    c.Author.ReprShort(),
		"timestamp": c.Timestamp,
		"reply_to":  c.ReplyTo,
		"contents":  c.Contents,
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
	err := db.QueryRow("SELECT "+
		"post.*, mine_user.nickname, mine_user.avatar "+
		"FROM post INNER JOIN mine_user ON post.author_id = mine_user.id "+
		"WHERE post.id = $1", p.Id,
	).Scan(
		&p.Id, &p.Author.Id, &p.Timestamp, &p.Type,
		&p.IsPublished, &p.Caption, &p.Contents,
		&p.Author.Nickname, &p.Author.Avatar,
	)
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
	if err := rows.Err(); err != nil {
		return err
	}

	err = db.QueryRow(
		"SELECT COUNT(*) FROM post_upvote WHERE post_id = $1", p.Id,
	).Scan(&p.UpvoteCount)
	if err != nil {
		return err
	}

	// TODO: optimize
	err = db.QueryRow(
		"SELECT COUNT(*) FROM comment WHERE post_id = $1", p.Id,
	).Scan(&p.CommentCount)
	return err
}

func (p *Post) Upvote(u User, upvote bool) error {
	var err error
	if upvote {
		_, err = db.Exec(
			"INSERT INTO post_upvote (post_id, user_id) VALUES ($1, $2) ON CONFLICT DO NOTHING",
			p.Id, u.Id)
	} else {
		_, err = db.Exec(
			"DELETE FROM post_upvote WHERE post_id = $1 AND user_id = $2",
			p.Id, u.Id)
	}
	if err != nil {
		if err, ok := err.(*pq.Error); ok && false {
			if err.Code.Class() == "23" && err.Constraint == "post_ref" {
				return CheckedError{404}
			}
		}
		return err
	}

	err = db.QueryRow(
		"SELECT COUNT(*) FROM post_upvote WHERE post_id = $1", p.Id,
	).Scan(&p.UpvoteCount)
	return err
}

func (c *Comment) Create() error {
	c.Timestamp = time.Now().Unix()
	err := db.QueryRow("INSERT INTO "+
		"comment (post_id, author_id, timestamp, reply_to, reply_root, contents) "+
		"VALUES ($1, $2, $3, NULLIF($4, -1), "+
		"  COALESCE((SELECT reply_root FROM comment WHERE id = $4), NULLIF($4, -1)), $5"+
		") RETURNING id",
		c.Post.Id, c.Author.Id, c.Timestamp, c.ReplyTo, c.Contents,
	).Scan(&c.Id)
	return err
}

const commentSelectClause = "SELECT " +
	"comment.id, comment.post_id, comment.author_id, comment.timestamp, " +
	"COALESCE(comment.reply_to, -1), " +
	"COALESCE(comment.reply_root, -1), " +
	"comment.contents, " +
	"mine_user.nickname, mine_user.avatar " +
	"FROM comment INNER JOIN mine_user ON comment.author_id = mine_user.id "

func (c *Comment) Read() error {
	err := db.QueryRow(commentSelectClause+
		"WHERE comment.id = $1", c.Id,
	).Scan(
		&c.Id, &c.Post.Id, &c.Author.Id, &c.Timestamp, &c.ReplyTo, &c.ReplyRoot,
		&c.Contents,
		&c.Author.Nickname, &c.Author.Avatar,
	)
	return err
}

func ReadComments(postId int, start int, count int, replyRoot int) ([]map[string]interface{}, error) {
	replyRootCond := "comment.reply_root IS NULL"
	queryArgs := []interface{}{postId, start, count}
	if replyRoot != -1 {
		replyRootCond = "comment.reply_root = $4"
		queryArgs = append(queryArgs, replyRoot)
	}
	rows, err := db.Query(commentSelectClause+
		"WHERE comment.post_id = $1 AND "+replyRootCond+" "+
		"ORDER BY comment.timestamp DESC, comment.id DESC "+
		"LIMIT $3 OFFSET $2",
		queryArgs...,
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	comments := []map[string]interface{}{}
	for rows.Next() {
		c := Comment{}
		err := rows.Scan(
			&c.Id, &c.Post.Id, &c.Author.Id, &c.Timestamp, &c.ReplyTo, &c.ReplyRoot,
			&c.Contents,
			&c.Author.Nickname, &c.Author.Avatar,
		)
		if err != nil {
			return nil, err
		}
		comments = append(comments, c.Repr())
	}
	return comments, rows.Err()
}
