#Create an Scada-LTS.war file and deploy it into Docker Tomcat Image.
FROM tomcat:8.5.58
LABEL maintainer="rjajko@softq.pl"
COPY WebContent/WEB-INF/lib/mysql-connector-java-3.1.8-bin.jar /usr/local/tomcat/lib/mysql-connector-java-3.1.8-bin.jar
COPY WebContent/WEB-INF/lib/mysql-connector-java-5.1.38-bin.jar /usr/local/tomcat/lib/mysql-connector-java-5.1.38-bin.jar
COPY docker/config/context.xml /usr/local/tomcat/conf/context.xml
COPY build/libs/Scada-LTS.war /usr/local/tomcat/webapps/