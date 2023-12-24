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

