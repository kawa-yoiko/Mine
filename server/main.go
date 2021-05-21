package main

import (
	"github.com/kawa-yoiko/Mine/server/models"
	"github.com/kawa-yoiko/Mine/server/routes"

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
	JwtSecret  string `json:"jwt_secret"`
	UploadDir  string `json:"upload_dir"`
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

	models.SetDB(db)
	if os.Getenv("RESET") != "" {
		if err := models.ResetDatabase(); err != nil {
			log.Fatalln(err)
		}
	}
	if err := models.InitializeSchemata(); err != nil {
		log.Fatalln(err)
	}

	// Start HTTP server
	if Config.Debug {
		routes.EnableResetEndpoint()
	}
	routes.JwtSecret = []byte(Config.JwtSecret)
	if err := routes.InitUpload(Config.UploadDir); err != nil {
		log.Fatalln(err)
	}
	http.HandleFunc("/", routes.GetRootRouterFunc())
	log.Printf("Listening on http://localhost:%d\n", Config.ServerPort)
	log.Fatal(http.ListenAndServe(fmt.Sprintf(":%d", Config.ServerPort), nil))
}
