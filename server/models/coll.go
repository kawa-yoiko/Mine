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

func (c *Collection) ReprBrief() map[string]interface{} {
	return map[string]interface{}{
		"id":    c.Id,
		"title": c.Title,
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
		id, caption, contents
		FROM post
		WHERE collection_id = $1 ORDER BY collection_seq`, c.Id)
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

func readCollections(userId int32) ([]map[string]interface{}, error) {
	rows, err := db.Query(`SELECT id, title, description
		FROM collection WHERE author_id = $1`, userId)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	collections := []map[string]interface{}{}
	for rows.Next() {
		c := Collection{}
		err := rows.Scan(&c.Id, &c.Title, &c.Description)
		if err != nil {
			return nil, err
		}
		collections = append(collections, c.ReprBrief())
	}
	if err = rows.Err(); err != nil {
		return nil, err
	}
	return collections, nil
}
