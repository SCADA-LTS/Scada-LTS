# ---------
# Build war
# We dont run or need any platform specific binaries in the build stage so skip the VM.
# ---------
FROM --platform=$BUILDPLATFORM gradle:8.5.0-jdk11-jammy AS build

# Install additional build dependences
# Install nvm with node and npm
ENV NVM_DIR /usr/local/nvm
ENV NODE_VERSION 14.20.1

RUN mkdir $NVM_DIR && curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.2/install.sh | bash \
    && . $NVM_DIR/nvm.sh \
    && nvm install $NODE_VERSION \
    && nvm alias default $NODE_VERSION \
    && nvm use default

ENV NODE_PATH $NVM_DIR/versions/node/v$NODE_VERSION/lib/node_modules
ENV PATH      $NVM_DIR/versions/node/v$NODE_VERSION/bin:$PATH

# Build
COPY . /build
WORKDIR /build
RUN gradle installWebDependency
RUN gradle war



# ---------
# Deploy it into Docker Tomcat Image.
# Platform specific image.
# ---------

FROM tomcat:9.0.86-jre11
LABEL maintainer="rjajko@softq.pl"

# Install dependences
RUN apt update && apt install -y unzip wait-for-it && apt clean && rm -rf /var/lib/apt/lists/*
COPY tomcat/lib/mysql-connector-java-5.1.49.jar /usr/local/tomcat/lib/mysql-connector-java-5.1.49.jar
COPY tomcat/lib/activation.jar /usr/local/tomcat/lib/activation.jar
COPY tomcat/lib/jaxb-api-2.4.0-b180830.0359.jar /usr/local/tomcat/lib/jaxb-api-2.4.0-b180830.0359.jar
COPY tomcat/lib/jaxb-core-3.0.2.jar /usr/local/tomcat/lib/jaxb-core-3.0.2.jar
COPY tomcat/lib/jaxb-runtime-2.4.0-b180830.0438.jar /usr/local/tomcat/lib/jaxb-runtime-2.4.0-b180830.0438.jar

# Add Scada-LTS
COPY --from=build /build/build/libs/Scada-LTS.war /usr/local/tomcat/webapps/
RUN cd /usr/local/tomcat/webapps/ && ls -l && mkdir Scada-LTS && unzip Scada-LTS.war -d Scada-LTS
COPY docker/config/context.xml /usr/local/tomcat/webapps/Scada-LTS/META-INF/context.xml