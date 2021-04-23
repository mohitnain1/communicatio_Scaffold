#FROM java:8
FROM openjdk:8-jre-alpine

LABEL maintainer="Oodles Technologies"

RUN apk --update add \
    fontconfig \
    ttf-dejavu 

ENV AWS_ACCESS_KEY_ID=AKIA2WBRMZFKJJZ4LRIU
ENV AWS_SECRET_ACCESS_KEY=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



COPY target/Communication-Scaffold-0.0.1-SNAPSHOT.jar /opt/dashspring.jar

run java  -jar   /opt/dashspring.jar


