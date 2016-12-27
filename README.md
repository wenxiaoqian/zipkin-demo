1、下载zipkin-server
   wget -O zipkin.jar  'https://search.maven.org/remote_content?g=io.zipkin.java&a=zipkin-server&v=LATEST&c=exec'  

2、启动zipkin-server
   java -jar zipkin-server-1.18.0-exec.jar
   启动带mysql
   java -jar zipkin-server-1.18.0-exec.jar --STORAGE_TYPE=mysql --MYSQL_HOST=localhost --MYSQL_TCP_PORT=3306 --MYSQL_DB=zipkin --MYSQL_USER=root --MYSQL_PASS=root

3、分别启动service1,service2,service3,service4