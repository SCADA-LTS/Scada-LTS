FROM ubuntu
RUN apt-get -y update
RUN apt-get -y install apt-utils openjdk-8-jdk net-tools vim dos2unix
ADD mysql /opt/installer
RUN chmod 777 /opt/installer/im.sh
RUN dos2unix /opt/installer/im.sh
RUN ls -l /opt/installer/*
RUN /opt/installer/im.sh
ADD app /opt/scadalts
ADD start.sh /root/
RUN dos2unix /root/start.sh
RUN chmod 777 /root/start.sh
CMD ["/root/start.sh"]
EXPOSE 8080
