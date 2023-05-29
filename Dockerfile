FROM openjdk:17-jdk-slim
LABEL maintainer="veselovnd@gmail.com"

WORKDIR /app

ARG JAR_FILE=build/libs/InstaZoo-0.0.1-SNAPSHOT.jar

ADD  ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]