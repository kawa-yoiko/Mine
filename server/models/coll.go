package models

type Collection struct {
	Id          int32
	Author      User
	Title       string
	Description string
	Tags        []string
	Posts       []Post
}

func init() {
	registerSchema("collection",
		"id SERIAL PRIMARY KEY",
		"author_id INTEGER NOT NULL",
		"title TEXT NOT NULL",
		"description TEXT NOT NULL",
		"ADD CONSTRAINT author_ref FOREIGN KEY (author_id) REFERENCES mine_user (id)",
	)
	registerSchema("collection_tag",
		"collection_id INTEGER NOT NULL",
		"tag TEXT NOT NULL",
		"ADD CONSTRAINT collection_ref FOREIGN KEY (collection_id) REFERENCES collection (id)",
	)
	registerSchema("collection_post",
		"collection_id INTEGER NOT NULL",
		"post_id INTEGER NOT NULL",
		"seq BIGSERIAL",
		"ADD CONSTRAINT collection_ref FOREIGN KEY (collection_id) REFERENCES collection (id)",
		"ADD CONSTRAINT post_ref FOREIGN KEY (post_id) REFERENCES post (id)",
	)
}

func (c *Collection) Repr() map[string]interface{} {
	posts := []map[string]interface{}{}
	for _, post := range c.Posts {
		posts = append(posts, post.ReprOutline())
	}
	return map[string]interface{}{
		"author":      c.Author.ReprBrief(),
		"title":       c.Title,
		"description": c.Description,
		"posts":       posts,
		"tags":        c.Tags,
	}
}

func (c *Collection) Create() error {
	err := db.QueryRow(`INSERT INTO
		collection (author_id, title, description)
		VALUES ($1, $2, $3) RETURNING id`,
		c.Author.Id, c.Title, c.Description,
	).Scan(&c.Id)
	if err != nil {
		return err
	}

	err = insertTags("collection_tag", "collection_id", c.Id, c.Tags)
	return err
}

func (c *Collection) Read() error {
	err := db.QueryRow(`SELECT
		collection.*, mine_user.nickname, mine_user.avatar
		FROM collection INNER JOIN mine_user ON collection.author_id = mine_user.id
		WHERE collection.id = $1`, c.Id,
	).Scan(
		&c.Id, &c.Author.Id, &c.Title, &c.Description,
		&c.Author.Nickname, &c.Author.Avatar,
	)
	if err != nil {
		return err
	}

	c.Tags, err = readTags("collection_tag", "collection_id", c.Id)
	if err != nil {
		return err
	}

	// Read posts
	rows, err := db.Query(`SELECT
		post.id, post.caption, post.contents
		FROM collection_post
		  INNER JOIN post ON collection_post.post_id = post.id
		WHERE collection_post.collection_id = $1 ORDER BY collection_post.seq`, c.Id)
	if err != nil {
		return err
	}
	defer rows.Close()
	posts := []Post{}
	for rows.Next() {
		p := Post{}
		err := rows.Scan(&p.Id, &p.Caption, &p.Contents)
		if err != nil {
			return err
		}
		posts = append(posts, p)
	}
	if err = rows.Err(); err != nil {
		return err
	}
	c.Posts = posts
	return nil
}

func (c *Collection) EditPosts(add bool, postId int32) error {
	var err error
	index := -1
	for i, p := range c.Posts {
		if p.Id == postId {
			index = i
			break
		}
	}
	if add && index == -1 {
		_, err = db.Exec(`INSERT INTO
			collection_post (collection_id, post_id)
			VALUES ($1, $2)`, c.Id, postId)
		if err != nil {
			return err
		}
		p := Post{Id: postId}
		if err = p.Read(); err != nil {
			return err
		}
		c.Posts = append(c.Posts, p)
	} else if !add && index != -1 {
		_, err = db.Exec(`DELETE FROM collection_post
			WHERE collection_id = $1 AND post_id = $2`, c.Id, postId)
		c.Posts = append(c.Posts[:index], c.Posts[index+1:]...)
	}
	return err
}
