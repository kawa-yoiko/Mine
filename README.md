# Mine

原创作品网站平台

[**演示视频**](https://github.com/kawa-yoiko/Mine/releases/download/demo-video/demo.mp4)

设计文档 → https://codimd.starrah.cn/aoXg7pwRThasaQZ1p4Vd1Q  
前后端接口 → https://codimd.starrah.cn/xu0f2xGAR6-snXnLnmKfSw

## 客户端程序编译

客户端程序源代码位于 `client` 目录下。

在 Android Studio 内打开项目然后编译即可。

## 服务端程序部署

服务端程序源代码位于 `server` 目录下。

为了保证安全性，首先需要设置 JWT 签名用的密钥。修改 `server/config.json` 中的 `jwt_secret` 一项为尽可能随机且长的字符串。

然后开始运行服务端程序。安装 Docker，然后执行：

```sh
docker build -t mineserver .
docker run --publish 7678:2317 -dt mineserver
```

服务端程序即在宿主的 7678 端口监听 HTTP 请求。也可以将 7678 替换为任意其他端口。

执行测试：

```sh
HOST=http://127.0.0.1:7678 node tests/test.js
```

生成随机数据：

```sh
HOST=http://127.0.0.1:7678 GEN=1 node tests/test.js
```
