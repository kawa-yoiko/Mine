package models

import (
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
	StarCount    int32
}

type Comment struct {
	Id        int32
	Post      Post
	Author    User
	Timestamp int64
	ReplyTo   int32
	ReplyRoot int32
	Contents  string

	ReplyUser User
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
		"ADD CONSTRAINT post_upvote_uniq UNIQUE (post_id, user_id)",
	)
	registerSchema("post_star",
		"post_id INTEGER NOT NULL",
		"user_id INTEGER NOT NULL",
		"ADD CONSTRAINT post_ref FOREIGN KEY (post_id) REFERENCES post (id)",
		"ADD CONSTRAINT user_ref FOREIGN KEY (user_id) REFERENCES mine_user (id)",
		"ADD CONSTRAINT post_star_uniq UNIQUE (post_id, user_id)",
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
		"author":        p.Author.ReprBrief(),
		"timestamp":     p.Timestamp,
		"type":          p.Type,
		"caption":       p.Caption,
		"contents":      p.Contents,
		"tags":          p.Tags,
		"upvote_count":  p.UpvoteCount,
		"comment_count": p.CommentCount,
		"star_count":    p.StarCount,
	}
}

func (p *Post) ReprOutline() map[string]interface{} {
	return map[string]interface{}{
		"caption":  p.Caption,
		"contents": p.Contents,
	}
}

func (c *Comment) Repr() map[string]interface{} {
	var replyUser interface{}
	if c.ReplyUser.Nickname == "" {
		replyUser = nil
	} else {
		replyUser = c.ReplyUser.ReprBrief()
	}
	return map[string]interface{}{
		"id":         c.Id,
		"author":     c.Author.ReprBrief(),
		"timestamp":  c.Timestamp,
		"reply_user": replyUser,
		"contents":   c.Contents,
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

	err = insertTags("post_tag", "post_id", p.Id, p.Tags)
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

	p.Tags, err = readTags("post_tag", "post_id", p.Id)
	if err != nil {
		return err
	}

	err = db.QueryRow(
		"SELECT COUNT(*) FROM post_upvote WHERE post_id = $1", p.Id,
	).Scan(&p.UpvoteCount)
	if err != nil {
		return err
	}

	err = db.QueryRow(
		"SELECT COUNT(*) FROM post_star WHERE post_id = $1", p.Id,
	).Scan(&p.StarCount)
	if err != nil {
		return err
	}

	// TODO: optimize
	err = db.QueryRow(
		"SELECT COUNT(*) FROM comment WHERE post_id = $1", p.Id,
	).Scan(&p.CommentCount)
	return err
}

func (p *Post) processUserRel(table string, u User, add bool, count *int32) error {
	var err error
	if add {
		_, err = db.Exec(
			"INSERT INTO "+table+" (post_id, user_id) VALUES ($1, $2) ON CONFLICT DO NOTHING",
			p.Id, u.Id)
	} else {
		_, err = db.Exec(
			"DELETE FROM "+table+" WHERE post_id = $1 AND user_id = $2",
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
		"SELECT COUNT(*) FROM "+table+" WHERE post_id = $1", p.Id,
	).Scan(count)
	return err
}

func (p *Post) Upvote(u User, add bool) error {
	return p.processUserRel("post_upvote", u, add, &p.UpvoteCount)
}

func (p *Post) Star(u User, add bool) error {
	return p.processUserRel("post_star", u, add, &p.StarCount)
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
	"author.nickname, author.avatar, " +
	"COALESCE(reply_user.nickname, ''), COALESCE(reply_user.avatar, '') " +
	"FROM comment " +
	"  INNER JOIN mine_user AS author ON comment.author_id = author.id " +
	"  LEFT JOIN comment AS reply_comment ON comment.reply_to = reply_comment.id " +
	"  LEFT JOIN mine_user AS reply_user ON reply_comment.author_id = reply_user.id "

func (c *Comment) Read() error {
	err := db.QueryRow(commentSelectClause+
		"WHERE comment.id = $1", c.Id,
	).Scan(
		&c.Id, &c.Post.Id, &c.Author.Id, &c.Timestamp, &c.ReplyTo, &c.ReplyRoot,
		&c.Contents,
		&c.Author.Nickname, &c.Author.Avatar,
		&c.ReplyUser.Nickname, &c.ReplyUser.Avatar,
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
			&c.ReplyUser.Nickname, &c.ReplyUser.Avatar,
		)
		if err != nil {
			return nil, err
		}
		comments = append(comments, c.Repr())
	}
	return comments, rows.Err()
}
