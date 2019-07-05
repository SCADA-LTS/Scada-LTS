# Container to build
FROM frekele/ant
RUN apt-get -y update
RUN apt-get -y install apt-utils curl dos2unix

RUN curl -sL https://deb.nodesource.com/setup_10.x | bash && apt-get install -y nodejs  

# add tomcat
WORKDIR /tmp
RUN curl -L http://www.strategylions.com.au/mirror/tomcat/tomcat-7/v7.0.94/bin/apache-tomcat-7.0.94.tar.gz --output apache-tomcat-7.0.94.tar.gz && tar -xvf apache-tomcat-7.0.94.tar.gz

# Add the files
WORKDIR /tmp
ADD . ./Scada-LTS
WORKDIR /tmp/Scada-LTS
RUN chmod 777 get_seroUtils.sh && dos2unix get_seroUtils.sh && ./get_seroUtils.sh
RUN export CATALINA_HOME=/tmp/apache-tomcat-7.0.94 && export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8 && ant war
######################################################################################################################
# Container to run
FROM ubuntu
RUN apt-get -y update
RUN apt-get -y install apt-utils openjdk-8-jdk net-tools vim dos2unix wget
COPY docker/mysql /opt/installer

RUN chmod 777 /opt/installer/im.sh
RUN dos2unix /opt/installer/im.sh
RUN ls -l /opt/installer/*
RUN /opt/installer/im.sh

WORKDIR /opt/scadalts
RUN wget http://www.strategylions.com.au/mirror/tomcat/tomcat-7/v7.0.94/bin/apache-tomcat-7.0.94.tar.gz && tar -xvf apache-tomcat-7.0.94.tar.gz --strip 1 && rm apache-tomcat-7.0.94.tar.gz

# Copy war file from build stage
COPY --from=0 /tmp/Scada-LTS/ScadaBR.war /opt/scadalts/webapps/ScadaBR.war

ADD docker/config/context.xml /opt/scadalts/conf

ADD docker/start.sh /root/
RUN dos2unix /root/start.sh
RUN chmod 777 /root/start.sh
CMD ["/root/start.sh"]
EXPOSE 8080