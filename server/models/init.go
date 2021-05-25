package models

import (
	"database/sql"
	"fmt"
	"strings"

	"github.com/lib/pq"
)

var db *sql.DB

type tableSchema struct {
	table   string
	columns []string
}

var schemata []tableSchema

func registerSchema(table string, columns ...string) {
	schemata = append(schemata, tableSchema{table, columns})
}

func SetDB(dbInput *sql.DB) {
	db = dbInput
}

func InitializeSchemata() error {
	for _, schema := range schemata {
		cmd := "CREATE TABLE IF NOT EXISTS " + schema.table + " ()"
		if _, err := db.Exec(cmd); err != nil {
			return err
		}
		for _, columnDesc := range schema.columns {
			columnName := strings.SplitN(columnDesc, " ", 2)[0]
			if columnName != "ADD" {
				// Column
				schema := "ALTER TABLE " + schema.table + " ADD COLUMN " + columnDesc
				if _, err := db.Exec(schema); err != nil {
					// https://www.postgresql.org/docs/13/errcodes-appendix.html
					if err, ok := err.(*pq.Error); ok && err.Code == "42701" {
						continue
					}
				}
			}
		}
	}
	for _, schema := range schemata {
		for _, columnDesc := range schema.columns {
			columnName := strings.SplitN(columnDesc, " ", 2)[0]
			if columnName == "ADD" {
				// Constraint
				schema := "ALTER TABLE " + schema.table + " " + columnDesc
				if _, err := db.Exec(schema); err != nil {
					if err, ok := err.(*pq.Error); ok &&
						(err.Code == "42P07" || err.Code == "42710") {
						continue
					}
					return err
				}
			}
		}
	}
	return nil
}

func ResetDatabase() error {
	for _, schema := range schemata {
		_, err := db.Exec("DROP TABLE IF EXISTS " + schema.table + " CASCADE")
		if err != nil {
			return err
		}
	}
	return InitializeSchemata()
}

type CheckedError struct{ Status int }

func (e CheckedError) Error() string {
	return "CheckedError"
}

func insertTags(table, field string, id int32, tags []string) error {
	if len(tags) == 0 {
		return nil
	}
	placeholders := strings.Builder{}
	values := []interface{}{}
	for i, tag := range tags {
		if tag == "" {
			continue
		}
		if i != 0 {
			fmt.Fprintf(&placeholders, ", ")
		}
		fmt.Fprintf(&placeholders, "($%d, $%d)", i*2+1, i*2+2)
		values = append(values, id, tag)
	}
	if len(values) == 0 {
		return nil
	}
	_, err := db.Exec(
		"INSERT INTO "+table+" ("+field+", tag) VALUES "+placeholders.String(),
		values...)
	return err
}

func readTags(table, field string, id int32) ([]string, error) {
	rows, err := db.Query("SELECT tag FROM "+table+" WHERE "+field+" = $1", id)
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
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return tags, nil
}
