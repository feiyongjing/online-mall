# online-mall
电商和支付双系统

生产环境
mysql docker服务启动
docker run --name mall -e MYSQL_DATABASE=mall -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:8.0.19

redis docker服务启动
docker run --name mall-redis -p 6379:6379 -d redis

rabbitmq docker服务启动
docker run --name rabbitmq -d -p 15672:15672 -p 5672:5672 -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin rabbitmq:3.9.5-management

测试环境
mysql docker服务启动
docker run --name mall-test -e MYSQL_DATABASE=mall-test -e MYSQL_ROOT_PASSWORD=root -p 3307:3306 -d mysql:8.0.19

redis docker服务启动
docker run --name mall-redis-test -p 6380:6379 -d redis

rabbitmq docker服务启动
docker run --name mall-rabbitmq-test -d -p 15673:15672 -p 5673:5672 -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin rabbitmq:3.9.5-management

