package routes

import (
	"github.com/kawa-yoiko/Mine/server/models"

	"errors"
	"github.com/google/uuid"
	"io"
	"net/http"
	"os"
)

var uploadDir string

func postUpload(w http.ResponseWriter, r *http.Request) {
	/*_, ok := auth(r)
	if !ok {
		panic(models.CheckedError{401})
	}*/

	reader, err := r.MultipartReader()
	if err != nil {
		println(err.Error())
		panic(models.CheckedError{400})
	}

	for {
		part, err := reader.NextPart()
		if err != nil {
			if errors.Is(err, io.EOF) {
				break
			}
			panic(err)
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
			l, err := part.Read(buf)
			if err != nil {
				if errors.Is(err, io.EOF) {
					break
				}
				panic(err)
			}
			_, err = f.Write(buf[:l])
			if err != nil {
				panic(err)
			}
			totalLen += l
		}
	}
}

func InitUpload(uploadDirInput string) error {
	// Create upload directory
	if err := os.MkdirAll(uploadDirInput, 0755); err != nil {
		return err
	}
	uploadDir = uploadDirInput
	return nil
}

func init() {
	registerHandler("/upload", postUpload, "POST")
}
