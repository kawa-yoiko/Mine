package main

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"

	"database/sql"
	_ "github.com/lib/pq"
)

var Config struct {
	ServerPort int    `json:"server_port"`
	DbName     string `json:"db_name"`
	DbUser     string `json:"db_user"`
	DbPassword string `json:"db_password"`
	Debug      bool   `json:"debug"`
}

func main() {
	log.SetFlags(log.Lshortfile)

	// Load configuration
	configPath := os.Getenv("CONFIG")
	if configPath == "" {
		configPath = "config.json"
	}
	content, err := os.ReadFile(configPath)
	if err != nil {
		log.Fatalln(err)
	}
	if err = json.Unmarshal(content, &Config); err != nil {
		log.Fatalln(err)
	}

	// Connect to database
	db, err := sql.Open("postgres",
		fmt.Sprintf("sslmode=disable dbname=%s user=%s password=%s",
			Config.DbName, Config.DbUser, Config.DbPassword),
	)
	if err != nil {
		log.Fatalln(err)
	}
	defer db.Close()

	if err := models.InitializeSchemata(db); err != nil {
		log.Fatalln(err)
	}

	// Listen on TCP port
	port := Config.ServerPort
	log.Printf("Listening on http://localhost:%d\n", port)
	log.Fatal(http.ListenAndServe(fmt.Sprintf(":%d", port), nil))
}
