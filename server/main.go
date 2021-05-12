package main

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"fmt"
	"log"
	"net/http"

	"database/sql"
	_ "github.com/lib/pq"
)

var db *sql.DB

func main() {
	log.SetFlags(log.Lshortfile)

	db, err := sql.Open("postgres",
		"sslmode=disable dbname=minedb")
	if err != nil {
		log.Fatalln(err)
	}
	defer db.Close()

	if err := models.InitializeSchemata(db); err != nil {
		log.Fatalln(err)
	}

	port := 2317
	log.Printf("Listening on http://localhost:%d\n", port)
	log.Fatal(http.ListenAndServe(fmt.Sprintf(":%d", port), nil))
}
