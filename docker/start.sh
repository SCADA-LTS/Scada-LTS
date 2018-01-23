#!/bin/bash
/etc/init.d/mysql start
#create database scadalts if not exist
sleep 10
mysql -uroot -proot -e "create database if not exists scadalts"
cd /opt/scadalts
./bin/startup.sh
sleep 60
cd
killall -9 java
cd /opt/scadalts/webapps/ScadaBR/WEB-INF/classes
sed -i 's/localhost:8090/'`echo $DOCKER_HOST_IP`:8090'/g' env.properties
cd
cd /opt/scadalts
./bin/startup.sh
cd
tail -f 100 /opt/scadalts/logs/catalina.out