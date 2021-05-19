FROM openjdk:8-jre-alpine

LABEL maintainer="Oodles Technologies"


#HEALTHCHECK --interval=50s --timeout=30s --retries=3 \
# CMD curl --silent --fail localhost:80 || exit 1

#RUN apt-get update
#RUN apt install openjdk-8-jdk
RUN apk --update add \
    fontconfig \
    ttf-dejavu 

#RUN mkdir -p /opt/oodles-profileimages/Feedback_folder

#RUN apk add git
#COPY ./spring  /opt/


COPY target/Communication-Scaffold-0.0.1-SNAPSHOT.jar /opt/Communication-Scaffold.jar

RUN md5sum /opt/Communication-Scaffold.jar

COPY ./docker/entrypoint.sh /

ENTRYPOINT java  -jar   /opt/Communication-Scaffold.jar