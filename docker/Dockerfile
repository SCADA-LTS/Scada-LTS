FROM ubuntu
RUN apt-get -y update
RUN apt-get -y install apt-utils && apt-get -y install openjdk-8-jdk && apt-get -y install net-tools && apt-get -y install vim
ADD mysql /opt/installer/
RUN /opt/installer/install_mysql.sh
ADD app /opt/scadalts
ADD start.sh /root/
CMD ["/root/start.sh"]
EXPOSE 8080
