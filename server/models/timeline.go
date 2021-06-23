package models

import (
	"database/sql"
	"strconv"
	"strings"
	"time"
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
		  ORDER BY post.timestamp DESC
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
		  ORDER BY post.timestamp DESC
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

func SearchPostsByTag(userId int32, tag string, start int, count int, ty string) ([]map[string]interface{}, error) {
	queryArgs := []interface{}{tag, start, count}
	queryFilter := ""
	queryOrder := ""
	switch ty {
	case "new":
		queryOrder = "post.timestamp DESC"
	case "day":
		queryFilter = " AND post.timestamp >= " + strconv.FormatInt(time.Now().Unix()-86400*1000*1, 10)
		queryOrder = "post_upvote_count DESC"
	case "month":
		queryFilter = " AND post.timestamp >= " + strconv.FormatInt(time.Now().Unix()-86400*1000*1, 10)
		queryOrder = "post_upvote_count DESC"
	case "season":
		queryFilter = " AND post.timestamp >= " + strconv.FormatInt(time.Now().Unix()-86400*1000*1, 10)
		queryOrder = "post_upvote_count DESC"
	case "all":
		queryOrder = "post_upvote_count DESC"
	default:
		return nil, CheckedError{400}
	}

	rows, err := db.Query(
		postSelectClauseWithBaseRel(
			userId,
			"post_tag INNER JOIN post ON post_tag.post_id = post.id",
		)+" WHERE post_tag.tag = $1 "+queryFilter+
			" ORDER BY "+queryOrder+
			" LIMIT $3 OFFSET $2",
		queryArgs...,
	)
	if err != nil {
		return nil, err
	}
	return postsReprBriefFromRows(rows)
}

func SearchCollectionsByTag(userId int32, tag string, start int, count int) ([]map[string]interface{}, error) {
	rows, err := db.Query(`SELECT`+collectionSelectFields()+`
		FROM collection_tag INNER JOIN collection
		  ON collection_tag.collection_id = collection.id
		  WHERE collection_tag.tag = $1
		  ORDER BY collection.id DESC
		  LIMIT $3 OFFSET $2`,
		tag, start, count,
	)
	if err != nil {
		return nil, err
	}
	return collectionsReprBriefFromRows(rows)
}

func SearchTags(tag string) ([]string, error) {
	tag = strings.ReplaceAll(tag, "%", "\\%")
	tag = strings.ReplaceAll(tag, "_", "\\_")
	rows, err := db.Query(
		`SELECT DISTINCT (tag) FROM post_tag WHERE tag LIKE $1 LIMIT 10`,
		"%"+tag+"%")
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	tags := []string{}
	for rows.Next() {
		var tag string
		if err := rows.Scan(&tag); err != nil {
			return nil, err
		}
		tags = append(tags, tag)
	}
	return tags, rows.Err()
}
