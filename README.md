# online-mall
电商和支付双系统

mysql docker服务启动
docker run --name mall -e MYSQL_DATABASE=mall -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:8.0.19