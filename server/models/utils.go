package models

import (
	"github.com/lib/pq"
	"time"
)

func processEntityUserRel(
	rel string, ent string, hasTimestamp bool,
	entId int32, u User, add bool, count *int32,
) error {
	var err error
	if add {
		if hasTimestamp {
			_, err = db.Exec(
				"INSERT INTO "+rel+" ("+ent+"_id, user_id, timestamp) VALUES ($1, $2, $3) ON CONFLICT DO NOTHING",
				entId, u.Id, time.Now().Unix())
		} else {
			_, err = db.Exec(
				"INSERT INTO "+rel+" ("+ent+"_id, user_id) VALUES ($1, $2) ON CONFLICT DO NOTHING",
				entId, u.Id)
		}
	} else {
		_, err = db.Exec(
			"DELETE FROM "+rel+" WHERE "+ent+"_id = $1 AND user_id = $2",
			entId, u.Id)
	}
	if err != nil {
		if err, ok := err.(*pq.Error); ok && false {
			if err.Code.Class() == "23" && err.Constraint == ent+"_ref" {
				return CheckedError{404}
			}
		}
		return err
	}

	err = db.QueryRow(
		"SELECT COUNT (*) FROM "+rel+" WHERE "+ent+"_id = $1", entId,
	).Scan(count)
	return err
}
