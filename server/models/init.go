package models

import (
	"database/sql"
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

func InitializeSchemata(dbInput *sql.DB) error {
	db = dbInput
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
					if pqErr, ok := err.(*pq.Error); ok && pqErr.Code == "42701" {
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
					if pqErr, ok := err.(*pq.Error); ok && pqErr.Code == "42P07" {
						continue
					}
					return err
				}
			}
		}
	}
	return nil
}
