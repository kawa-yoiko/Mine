package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"errors"
	"github.com/google/uuid"
	"github.com/gorilla/mux"
	"io"
	"net/http"
	"os"
)

var uploadDir string

func handleUpload(w http.ResponseWriter, r *http.Request) (models.User, []string) {
	u := mustAuth(r)

	reader, err := r.MultipartReader()
	if err != nil {
		panic(models.CheckedError{400})
	}

	files := []string{}
	for {
		part, err := reader.NextPart()
		if err != nil {
			if errors.Is(err, io.EOF) {
				break
			}
			panic(models.CheckedError{400})
		}

		fileId := uuid.New().String()
		f, err := os.Create(uploadDir + "/" + fileId)
		if err != nil {
			panic(err)
		}
		defer f.Close()

		buf := make([]byte, 1<<16)
		totalLen := 0
		for {
			l, errRead := part.Read(buf)
			_, err := f.Write(buf[:l])
			if err != nil {
				panic(err)
			}
			totalLen += l
			if errRead != nil {
				if errors.Is(errRead, io.EOF) {
					break
				}
				panic(models.CheckedError{400})
			}
		}
		files = append(files, fileId)
	}
	return u, files
}

func InitUpload(uploadDirInput string) error {
	// Create upload directory
	if err := os.MkdirAll(uploadDirInput, 0755); err != nil {
		return err
	}
	uploadDir = uploadDirInput
	return nil
}

func postUpload(w http.ResponseWriter, r *http.Request) {
	_, files := handleUpload(w, r)
	write(w, 200, jsonPayload{"ids": files})
}

func postUploadAvatar(w http.ResponseWriter, r *http.Request) {
	u, files := handleUpload(w, r)
	if len(files) != 1 {
		panic(models.CheckedError{400})
	}

	u.Avatar = files[0]
	if err := u.Update(); err != nil {
		panic(err)
	}

	write(w, 200, u.Repr())
}

func postUploadCollectionCover(w http.ResponseWriter, r *http.Request) {
	u, files := handleUpload(w, r)
	if len(files) != 1 {
		panic(models.CheckedError{400})
	}

	c := referredCollection(u, mux.Vars(r)["id"])
	c.Cover = files[0]
	if err := c.Update(); err != nil {
		panic(err)
	}

	write(w, 200, c.Repr())
}

func init() {
	registerHandler("/upload", postUpload, "POST")
	registerHandler("/upload/avatar", postUploadAvatar, "POST")
	registerHandler("/upload/collection_cover/{id}", postUploadCollectionCover, "POST")

	router.PathPrefix("/upload").Handler(
		http.FileServer(http.Dir(uploadDir))).Methods("GET")
}
