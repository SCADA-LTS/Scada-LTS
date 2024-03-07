#Create an Scada-LTS.war file and deploy it into Docker Tomcat Image.
FROM tomcat:9.0.86-jre11
LABEL maintainer="rjajko@softq.pl"
COPY tomcat/lib/mysql-connector-java-5.1.49.jar /usr/local/tomcat/lib/mysql-connector-java-5.1.49.jar
COPY tomcat/lib/activation.jar /usr/local/tomcat/lib/activation.jar
COPY tomcat/lib/jaxb-api-2.4.0-b180830.0359.jar /usr/local/tomcat/lib/jaxb-api-2.4.0-b180830.0359.jar
COPY tomcat/lib/jaxb-core-3.0.2.jar /usr/local/tomcat/lib/jaxb-core-3.0.2.jar
COPY tomcat/lib/jaxb-runtime-2.4.0-b180830.0438.jar /usr/local/tomcat/lib/jaxb-runtime-2.4.0-b180830.0438.jar

COPY build/libs/Scada-LTS.war /usr/local/tomcat/webapps/
RUN apt update;
RUN apt install -y unzip;
RUN apt install -y wait-for-it;
RUN apt clean;
RUN rm -rf /var/lib/apt/lists/*;
RUN ls -l;
RUN cd /usr/local/tomcat/webapps/ && mkdir Scada-LTS && unzip Scada-LTS.war -d Scada-LTS;
RUN ls -l;
COPY docker/config/context.xml /usr/local/tomcat/webapps/Scada-LTS/META-INF/context.xml