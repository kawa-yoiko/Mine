package models

import (
	"database/sql"
)

func postsReprBriefFromRows(rows *sql.Rows) ([]map[string]interface{}, error) {
	var err error
	defer rows.Close()
	posts := []map[string]interface{}{}
	for rows.Next() {
		p := Post{}
		if err = rows.Scan(p.fields()...); err != nil {
			return nil, err
		}
		if p.Tags, err = readTags("post_tag", "post_id", p.Id); err != nil {
			return nil, err
		}
		posts = append(posts, p.ReprBrief())
	}
	if err = rows.Err(); err != nil {
		return nil, err
	}
	return posts, nil
}

func SubscriptionTimeline(userId int32, start int, count int) ([]map[string]interface{}, error) {
	rows, err := db.Query(postSelectClause(userId)+
		`WHERE collection.id IN
		  (SELECT collection_id FROM collection_subscription WHERE user_id = $1)
		  ORDER BY post.timestamp
		  LIMIT $3 OFFSET $2`,
		userId, start, count)
	if err != nil {
		return nil, err
	}
	return postsReprBriefFromRows(rows)
}

func DiscoverTimeline(userId int32, start int, count int) ([]map[string]interface{}, error) {
	rows, err := db.Query(postSelectClause(userId)+
		`WHERE collection.id NOT IN
		  (SELECT collection_id FROM collection_subscription WHERE user_id = $1)
		  ORDER BY post.timestamp
		  LIMIT $3 OFFSET $2`,
		userId, start, count)
	if err != nil {
		return nil, err
	}
	return postsReprBriefFromRows(rows)
}

func StarTimeline(userId int32, start int, count int) ([]map[string]interface{}, error) {
	rows, err := db.Query(`SELECT
		post.id, post.type, post.caption, post.contents, post_star.timestamp
		FROM post
		  INNER JOIN post_star ON post.id = post_star.post_id
		WHERE post_star.user_id = $1
		ORDER BY post_star.timestamp DESC
		LIMIT $3 OFFSET $2
	`, userId, start, count)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	posts := []map[string]interface{}{}
	for rows.Next() {
		p := Post{}
		var timestamp int64
		if err := rows.Scan(&p.Id, &p.Type, &p.Caption, &p.Contents, &timestamp); err != nil {
			return nil, err
		}
		posts = append(posts, map[string]interface{}{
			"post":      p.ReprOutline(),
			"timestamp": timestamp,
		})
	}
	if err = rows.Err(); err != nil {
		return nil, err
	}
	return posts, nil
}
