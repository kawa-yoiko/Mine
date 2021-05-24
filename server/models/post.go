package models

import (
	"strings"
	"time"
)

type Post struct {
	Id           int32
	Author       User
	Timestamp    int64
	Type         int32
	Caption      string
	Contents     string
	Collection   Collection
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

	ReplyUser  User
	ReplyCount int32
}

func init() {
	registerSchema("post",
		"id SERIAL PRIMARY KEY",
		"author_id INTEGER NOT NULL",
		"timestamp BIGINT NOT NULL",
		"type INTEGER NOT NULL",
		"caption TEXT NOT NULL",
		"contents TEXT NOT NULL",
		"collection_id INTEGER NOT NULL",
		"collection_seq INTEGER NOT NULL",
		"ADD CONSTRAINT author_ref FOREIGN KEY (author_id) REFERENCES mine_user (id)",
		"ADD CONSTRAINT collection_ref FOREIGN KEY (collection_id) REFERENCES collection (id)",
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
		"collection":    p.Collection.ReprBrief(),
		"tags":          p.Tags,
		"upvote_count":  p.UpvoteCount,
		"comment_count": p.CommentCount,
		"star_count":    p.StarCount,
	}
}

func (p *Post) truncatedContents(limit int) string {
	// Truncate contents
	if p.Type == 0 {
		numRunes := 0
		for i, _ := range p.Contents {
			if numRunes++; numRunes > limit {
				return p.Contents[:i]
			}
		}
	} else if p.Type == 1 {
		return strings.SplitN(p.Contents, " ", 2)[0]
	}
	return p.Contents
}

func (p *Post) ReprBrief() map[string]interface{} {
	ret := p.Repr()
	ret["id"] = p.Id
	ret["contents"] = p.truncatedContents(300)
	return ret
}

func (p *Post) ReprOutline() map[string]interface{} {
	ret := map[string]interface{}{
		"id":       p.Id,
		"type":     p.Type,
		"contents": p.truncatedContents(100),
	}
	if p.Type == 0 {
		ret["caption"] = p.Caption
	}
	return ret
}

func (c *Comment) Repr() map[string]interface{} {
	ret := map[string]interface{}{
		"id":        c.Id,
		"author":    c.Author.ReprBrief(),
		"timestamp": c.Timestamp,
		"contents":  c.Contents,
	}
	if c.ReplyUser.Nickname == "" {
		ret["reply_count"] = c.ReplyCount
	} else {
		ret["reply_user"] = c.ReplyUser.ReprBrief()
	}
	return ret
}

func (p *Post) Create() error {
	p.Timestamp = time.Now().Unix()
	err := db.QueryRow("INSERT INTO "+
		"post (author_id, timestamp, type, caption, contents, "+
		"  collection_id, collection_seq) "+
		"VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING id",
		p.Author.Id, p.Timestamp, p.Type, p.Caption, p.Contents,
		p.Collection.Id, 1234,
	).Scan(&p.Id)
	if err != nil {
		return err
	}

	err = insertTags("post_tag", "post_id", p.Id, p.Tags)
	return err
}

// TODO: Optimize comment counting
const postSelectClause = `SELECT
	post.*, mine_user.nickname, mine_user.avatar, collection.title,
	(SELECT COUNT (*) FROM post_upvote WHERE post_upvote.post_id = post.id),
	(SELECT COUNT (*) FROM post_star WHERE post_star.post_id = post.id),
	(SELECT COUNT (*) FROM comment WHERE comment.post_id = post.id)
	FROM post INNER JOIN mine_user ON post.author_id = mine_user.id
	  INNER JOIN collection ON post.collection_id = collection.id
`

func (p *Post) fields() []interface{} {
	var collectionSeq int32
	return []interface{}{
		&p.Id, &p.Author.Id, &p.Timestamp, &p.Type,
		&p.Caption, &p.Contents,
		&p.Collection.Id, &collectionSeq,
		&p.Author.Nickname, &p.Author.Avatar,
		&p.Collection.Title,
		&p.UpvoteCount, &p.StarCount, &p.CommentCount,
	}
}

func (p *Post) Read() error {
	err := db.QueryRow(
		postSelectClause+"WHERE post.id = $1", p.Id,
	).Scan(p.fields()...)
	if err != nil {
		return err
	}

	p.Tags, err = readTags("post_tag", "post_id", p.Id)
	if err != nil {
		return err
	}

	return nil
}

func (p *Post) Upvote(u User, add bool) error {
	return processEntityUserRel("post_upvote", "post", p.Id, u, add, &p.UpvoteCount)
}

func (p *Post) Star(u User, add bool) error {
	return processEntityUserRel("post_star", "post", p.Id, u, add, &p.StarCount)
}

func (p *Post) SetCollection(c Collection) error {
	_, err := db.Exec(`UPDATE post SET
		collection_id = $1, collection_seq = $2
		WHERE id = $3`,
		c.Id, 1234, p.Id)
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
	"author.nickname, author.avatar, " +
	"COALESCE(reply_user.nickname, ''), COALESCE(reply_user.avatar, ''), " +
	"(SELECT COUNT (*) FROM comment AS c1 WHERE c1.reply_root = comment.id) " +
	"FROM comment " +
	"  INNER JOIN mine_user AS author ON comment.author_id = author.id " +
	"  LEFT JOIN comment AS reply_comment ON comment.reply_to = reply_comment.id " +
	"  LEFT JOIN mine_user AS reply_user ON reply_comment.author_id = reply_user.id "

func (c *Comment) fields() []interface{} {
	return []interface{}{
		&c.Id, &c.Post.Id, &c.Author.Id, &c.Timestamp, &c.ReplyTo, &c.ReplyRoot,
		&c.Contents,
		&c.Author.Nickname, &c.Author.Avatar,
		&c.ReplyUser.Nickname, &c.ReplyUser.Avatar,
		&c.ReplyCount,
	}
}

func (c *Comment) Read() error {
	err := db.QueryRow(commentSelectClause+
		"WHERE comment.id = $1", c.Id,
	).Scan(c.fields()...)
	return err
}

func ReadComments(postId int32, start int, count int, replyRoot int) ([]map[string]interface{}, error) {
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
		err := rows.Scan(c.fields()...)
		if err != nil {
			return nil, err
		}
		comments = append(comments, c.Repr())
	}
	return comments, rows.Err()
}
