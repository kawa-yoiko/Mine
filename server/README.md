## Docker setup (for production)
```sh
docker build -t mineserver .
docker run --publish 7678:2317 -dt mineserver
# Testing
HOST=http://8.140.133.34:7678 node tests/test.js
```

## Local PostgreSQL setup (for development)
```sh
initdb -D data.swp
pg_ctl -D data.swp start
createdb minedb
createuser mineserver
```
