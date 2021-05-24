## Docker setup (for production)
```sh
docker build -t mineserver .
docker run --publish 7678:2317 -dt mineserver --name mineins
# Testing
HOST=http://8.140.133.34:7678 node tests/test.js
# Generate dataset
HOST=http://8.140.133.34:7678 GEN=1 node tests/test.js
```

To stop and remove container:
```sh
docker stop mineins
docker rm mineins
docker image rm mineserver
```

## Local PostgreSQL setup (for development)
```sh
initdb -D data.swp
pg_ctl -D data.swp start
createdb minedb
createuser mineserver
```
