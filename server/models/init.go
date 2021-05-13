package models

import (
	"database/sql"
	"strings"
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
				row := db.QueryRow("SELECT COUNT(*) FROM information_schema.columns "+
					"WHERE table_name = $1 AND column_name = $2",
					schema.table,
					columnName,
				)
				var count int
				if err := row.Scan(&count); err != nil {
					return err
				}
				if count == 0 {
					schema := "ALTER TABLE " + schema.table + " ADD COLUMN " + columnDesc
					if _, err := db.Exec(schema); err != nil {
						return err
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
					return err
				}
			}
		}
	}
	return nil
}
