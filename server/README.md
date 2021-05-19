## Docker setup
Dockerfile
```sh
docker build -t mineserver .
docker run --rm --publish 7678:2317 -it mineserver
```

## Testing
```sh
HOST=http://8.140.133.34:7678 node tests/test.js
```

## PostgreSQL setup
```sh
initdb -D data.swp
postgres -D data.swp

createdb minedb
createuser mineserver
```

## Miscellaneous Docker configurations for reference
PostgreSQL only
```sh
docker create --name pg1 --publish 5432:7677 --tty --interactive --env POSTGRES_USER=mineserver --env POSTGRES_DB=minedb --env POSTGRES_PASSWORD=qwqwqwqwq postgres
docker start pg1
```

Everything
```sh
docker create --name mine1 --publish 7678:2317 --tty --interactive alpine
docker cp ~/.ssh mine1:/root/.ssh
docker start mine1

# sed -i -e 's/dl-.*.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' -e 's/v[[:digit:]]\..*\//edge\//g' /etc/apk/repositories
# apk add openssh-client git go postgresql

# chown -R root ~/.ssh
# git clone git@github.com:kawa-yoiko/Mine.git

# cd Mine/server
# GOPROXY=https://goproxy.io,direct go get .

# adduser -D kyyk
# mkdir data.swp /run/postgresql
# chown -R kyyk data.swp /run/postgresql
# su kyyk -c "initdb -D data.swp"
# su kyyk -c "pg_ctl -D data.swp start"
# su kyyk -c "createdb minedb"
# su kyyk -c "createuser mineserver"
```
