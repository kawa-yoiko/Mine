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
	registerSchema("message_pair",
		"to_user_id INTEGER NOT NULL",
		"from_user_id INTEGER", // nullable
		"unread_count INTEGER NOT NULL",
		"ADD CONSTRAINT to_user_ref FOREIGN KEY (to_user_id) REFERENCES mine_user (id)",
		"ADD CONSTRAINT from_user_ref FOREIGN KEY (from_user_id) REFERENCES mine_user (id)",
		"ADD CONSTRAINT message_pair_uniq UNIQUE (to_user_id, from_user_id)",
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

func (m *Message) ReprBrief(fromMe bool) map[string]interface{} {
	return map[string]interface{}{
		"id":        m.Id,
		"timestamp": m.Timestamp,
		"from_me":   fromMe,
		"contents":  m.Contents,
	}
}

func (m *Message) Create() error {
	m.Timestamp = time.Now().Unix()
	err := db.QueryRow(`INSERT INTO
		message (timestamp, to_user_id, from_user_id, contents)
		VALUES ($1, $2, NULLIF($3, -1), $4) RETURNING id`,
		m.Timestamp, m.ToUser.Id, m.FromUser.Id, m.Contents,
	).Scan(&m.Id)
	if err != nil {
		return err
	}
	extraPair := ", ($2, $1, 0)"
	if m.FromUser.Id == -1 {
		extraPair = ""
	}
	_, err = db.Exec(`INSERT INTO
		message_pair (to_user_id, from_user_id, unread_count)
		VALUES ($1, NULLIF($2, -1), 0)`+extraPair+` ON CONFLICT DO NOTHING`,
		m.ToUser.Id, m.FromUser.Id)
	if err != nil {
		return err
	}
	_, err = db.Exec(`UPDATE message_pair
		SET unread_count = unread_count + 1
		WHERE to_user_id = $1 AND COALESCE(from_user_id, -1) = $2`,
		m.ToUser.Id, m.FromUser.Id)
	return err
}

func (m *Message) fields() []interface{} {
	return []interface{}{
		&m.Id, &m.Timestamp, &m.ToUser.Id, &m.FromUser.Id, &m.Contents,
		&m.FromUser.Nickname, &m.FromUser.Avatar,
		&m.ToUser.Nickname, &m.ToUser.Avatar,
	}
}

func ReadMessagesBetweenUsers(selfUserId int32, otherUserId int32, start int, count int) ([]map[string]interface{}, error) {
	rows, err := db.Query(`SELECT
		id, timestamp, to_user_id, COALESCE(from_user_id, -1), contents
		FROM message
		WHERE (COALESCE(from_user_id, -1) = $1 AND to_user_id = $2) OR
		      (COALESCE(from_user_id, -1) = $2 AND to_user_id = $1)
		ORDER BY timestamp DESC, id DESC
		LIMIT $3 OFFSET $4`,
		selfUserId, otherUserId, count, start)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	messages := []map[string]interface{}{}
	for rows.Next() {
		m := Message{}
		if err := rows.Scan(m.fields()[:5]...); err != nil {
			return nil, err
		}
		messages = append(messages, m.ReprBrief(m.FromUser.Id == selfUserId))
	}
	return messages, rows.Err()
}

// Does not return system messages
func ReadLatestMessages(userId int32) ([]map[string]interface{}, error) {
	/* ERROR:  subquery must return only one column
	rows, err := db.Query(`SELECT
		(SELECT * FROM message
		  WHERE message.to_user_id = $1
		    AND message.from_user_id = message_pair.from_user_id
		  ORDER BY timestamp DESC LIMIT 1)
		FROM message_pair
		WHERE to_user_id = $1`, userId)
	*/
	rows, err := db.Query(`SELECT * FROM (
		SELECT DISTINCT ON (message_pair.from_user_id)
		  message_pair.unread_count,
		  message.*,
		  from_user.nickname, from_user.avatar,
		  to_user.nickname, to_user.avatar
		FROM message_pair
		  INNER JOIN message ON
		    (message.from_user_id = message_pair.from_user_id AND
		     message.to_user_id = message_pair.to_user_id) OR
		    (message.from_user_id = message_pair.to_user_id AND
		     message.to_user_id = message_pair.from_user_id)
		  LEFT JOIN mine_user from_user ON message.from_user_id = from_user.id
		  INNER JOIN mine_user to_user ON message.to_user_id = to_user.id
		WHERE message_pair.to_user_id = $1
		ORDER BY message_pair.from_user_id, message.timestamp DESC, message.id DESC
	) AS _
		ORDER BY timestamp DESC, id DESC`, userId)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	messages := []map[string]interface{}{}
	for rows.Next() {
		m := Message{}
		var unreadCount int
		fields := append([]interface{}{&unreadCount}, m.fields()...)
		if err := rows.Scan(fields...); err != nil {
			return nil, err
		}
		r := m.Repr()
		r["unread_count"] = unreadCount
		messages = append(messages, r)
	}
	return messages, rows.Err()
}

func MarkMessagesAsRead(toUserId, fromUserId int32) error {
	_, err := db.Exec(`UPDATE message_pair
		SET unread_count = 0 WHERE to_user_id = $1 AND from_user_id = $2`,
		toUserId, fromUserId)
	return err
}

func SendSystemMessage(userId int32, contents string) error {
	m := Message{
		ToUser:   User{Id: userId},
		FromUser: User{Id: -1},
		Contents: contents,
	}
	return m.Create()
}
