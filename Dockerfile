FROM node:21.5.0-alpine3.19 as vuejs
WORKDIR /src
COPY ./scadalts-ui .
RUN npm install
RUN npm run build
CMD echo "Javascript files compiled with success"

FROM maven:3.9.6-amazoncorretto-11-al2023 as maven
WORKDIR /scadalts
COPY . .
COPY --from=vuejs /scadalts-ui/dist scadalts-ui
RUN cp scadalts-ui/dist/css/*.css WebContent/resources/js-ui/app/css

RUN cp scadalts-ui/dist/js/chunk-vendors.js WebContent/resources/js-ui/app/js
RUN cp scadalts-ui/dist/js/app.js WebContent/resources/js-ui/app/js

RUN cp scadalts-ui/dist/js/pdfmake.js WebContent/resources/js-ui/modernWatchList/js
RUN cp scadalts-ui/dist/js/xlsx.js WebContent/resources/js-ui/modernWatchList/js
RUN cp scadalts-ui/dist/js/canvg.js WebContent/resources/js-ui/modernWatchList/js
RUN cp scadalts-ui/dist/js/example-chart-cmp.js WebContent/resources/js-ui/modernWatchList/js

RUN cp scadalts-ui/dist/js/simple-component-svg.js WebContent/resources/js-ui/views/js
RUN cp scadalts-ui/dist/js/live-alarms-component.js WebContent/resources/js-ui/views/js
RUN cp scadalts-ui/dist/js/isalive-component.js WebContent/resources/js-ui/views/js
RUN cp scadalts-ui/dist/js/test-component.js WebContent/resources/js-ui/views/js
RUN cp scadalts-ui/dist/js/cmp-component-svg.js WebContent/resources/js-ui/views/js

RUN cp scadalts-ui/dist/js/sleep-and-reactivation-ds-component.js WebContent/resources/js-ui/ds/js

RUN cp scadalts-ui/dist/js/ph.js WebContent/resources/js-ui/pointHierarchy/js

RUN cp scadalts-ui/dist/fonts WebContent/resources/js-ui/app/fonts

RUN cp scadalts-ui/dist/img WebContent/img

#COPY --from=scadalts-ui /src/* ./scadalts-ui/
RUN mvn package
 
FROM scratch as war-file
WORKDIR /
COPY --from=maven /home/gradle/build/libs/Scada-LTS.war/ Scada-LTS.war

FROM tomcat:9.0.53 as scadalts-tomcat
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
RUN apt update && apt install -y wait-for-it && apt clean && rm -rf /var/lib/apt/lists/*

