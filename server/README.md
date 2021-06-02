## Docker setup (for production)
```sh
docker build -t mineserver .
docker run --publish 7678:2317 -dt mineserver
# Testing
HOST=http://8.140.133.34:7678 node tests/test.js
HOST=http://127.0.0.1:7678 node tests/test.js
# Generate dataset
HOST=http://8.140.133.34:7678 GEN=1 node tests/test.js
HOST=http://127.0.0.1:7678 GEN=1 node tests/test.js
```

To stop and remove container:
```sh
docker rm $(docker stop $(docker ps -a -q --filter ancestor=mineserver --format="{{.ID}}"))
docker image rm mineserver
```

To start a test server on an alternative port:
```sh
docker run -it --entrypoint=sh --publish 7679:2317 mineserver
```

## Local PostgreSQL setup (for development)
```sh
initdb -D data.swp
pg_ctl -D data.swp start
createdb minedb
createuser mineserver
```
