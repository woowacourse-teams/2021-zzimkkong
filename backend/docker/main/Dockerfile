FROM ubuntu:18.04

LABEL email="ssangyu123@gmail.com"
LABEL name="sakjung"
LABEL description="zzimkkong main application"

RUN	apt-get -y update
RUN apt-get install -y openjdk-11-jdk

# run application
WORKDIR /home/ubuntu
COPY build/libs/backend-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir zzimkkong && mkdir zzimkkong/tmp

EXPOSE 	8080

# 빌드시 명령어에 옵션 추가: --build-arg CLUSTER_IP=<cluster ip>
ENV CLUSTER_IP="IP"

ENTRYPOINT ["java", "-Ds3proxy.server-uri=http://${CLUSTER_IP}", "-Dspring.profiles.active=prod", "-jar", "app.jar", "--spring.config.location=classpath:config/application-prod.properties"]
