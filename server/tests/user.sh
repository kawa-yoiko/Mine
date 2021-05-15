# 200
curl -i -X POST http://localhost:2317/reset
# 400 {"err":1}
curl -i -X POST http://localhost:2317/signup -d "nickname=kayuyuko1&email=kyyk1@kawa..moe&password=888888"
# 400 {"err":1}
curl -i -X POST http://localhost:2317/signup -d "nickname=kayuyuko1&email=kyyk1@kawa.moe&password=哈哈哈哈哈哈"
# 400 {"err":1}
curl -i -X POST http://localhost:2317/signup -d "nickname=kayuyuko1&email=kyyk1@kawa.moe&password=888"
# 200 {"err":0}
curl -i -X POST http://localhost:2317/signup -d "nickname=kayuyuko1&email=kyyk1@kawa.moe&password=888888"
# 400 {"err":2}
curl -i -X POST http://localhost:2317/signup -d "nickname=kayuyuko1&email=kyyk2@kawa.moe&password=888888"
# 400 {"err":3}
curl -i -X POST http://localhost:2317/signup -d "nickname=kayuyuko2&email=kyyk1@kawa.moe&password=888888"
# 200 {"err":0}
curl -i -X POST http://localhost:2317/signup -d "nickname=kayuyuko2&email=kyyk2@kawa.moe&password=888888"
# 400 {"err":1}
curl -i -X POST http://localhost:2317/signup -d "nickname=小猫&email=kyyk3@kawa.moe&password=888888"
# 200 {"err":0}
curl -i -X POST http://localhost:2317/signup -d "nickname=栗小猫&email=kyyk3@kawa.moe&password=888888"
