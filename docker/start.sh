#!/bin/bash
/etc/init.d/mysql start
#create database scadalts if not exist
sleep 10
mysql -uroot -proot -e "create database if not exists scadalts"
cd /opt/scadalts
./bin/startup.sh
tail -f 100 /opt/scadalts/logs/catalina.out
