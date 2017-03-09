FROM docker/whalesay:latest
RUN apt-get -y update
RUN apt-get install tomcat7