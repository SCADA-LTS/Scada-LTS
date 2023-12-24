FROM node:21.4 as scadalts-ui
WORKDIR /src
COPY ./scadalts-ui .
RUN npm install
RUN npm run build
CMD echo "Javascript files compiled with success"

FROM gradle:7.3 as war-compiler
WORKDIR /home/gradle
COPY . .
COPY --from=scadalts-ui /src/* ./scadalts-ui/
RUN gradle war
CMD echo "War file compiled with success"

FROM scratch as war-file
WORKDIR /
COPY --from=war-compiler /home/gradle/build/libs/Scada-LTS.war/ Scada-LTS.war

FROM tomcat:9.0.53 as scadalts-tomcat
LABEL maintainer="rjajko@softq.pl"
WORKDIR /usr/local/tomcat/
COPY WebContent/WEB-INF/lib/mysql-connector-java-5.1.49.jar ./lib/mysql-connector-java-5.1.49.jar
COPY tomcat/lib/activation.jar ./lib/activation.jar
COPY tomcat/lib/jaxb-api-2.4.0-b180830.0359.jar ./lib/jaxb-api-2.4.0-b180830.0359.jar
COPY tomcat/lib/jaxb-core-3.0.2.jar ./lib/jaxb-core-3.0.2.jar
COPY tomcat/lib/jaxb-runtime-2.4.0-b180830.0438.jar ./lib/jaxb-runtime-2.4.0-b180830.0438.jar
#COPY build/libs/Scada-LTS.war /usr/local/tomcat/webapps/
COPY --from=war-file /Scada-LTS.war ./webapps
RUN cd ./webapps/ && mkdir Scada-LTS && unzip Scada-LTS.war -d Scada-LTS
COPY docker/config/context.xml ./webapps/Scada-LTS/META-INF/context.xml
RUN apt update && apt install wait-for-it && apt clean && rm -rf /var/lib/apt/lists/*

