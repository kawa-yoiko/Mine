FROM alpine

RUN sed -i -e 's/dl-.*.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' -e 's/v[[:digit:]]\..*\//edge\//g' /etc/apk/repositories
RUN apk add go postgresql
RUN adduser -D kyyk
RUN mkdir /var/minedata /run/postgresql
RUN chown -R kyyk /var/minedata /run/postgresql
RUN su kyyk -c "initdb -D /var/minedata && pg_ctl -D /var/minedata start && createdb minedb && createuser mineserver"

COPY . /root/mineserver
RUN cd /root/mineserver && GOPROXY=https://goproxy.io,direct go get && go build

ENTRYPOINT su kyyk -c "pg_ctl -D /var/minedata start" && CONFIG=/root/mineserver/config.json /root/mineserver/server & sh
