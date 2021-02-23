# 构建命令
````
clean install -pl com.szmsd.oms:oms-business-bas -am -Dmaven.test.skip=true -Pdev
````
# 启动命令
````
目录
/u01/www/skywin/oms/
启动命令
nohup java -Xms256m -Xmx512m -jar /u01/www/skywin/oms/oms-business-bas-1.0.0.jar --spring.profiles.active=test --server.port=9902 & > /dev/null 2>&1
````
````
47.115.117.247：6070
admin / $SDF8*sdfk23sdfKSDFL$t
````