FROM gradle:7.6.1-alpine AS BUILD
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon

FROM openjdk:17-jdk-slim

LABEL maintainer="veselovnd@gmail.com"

RUN mkdir /app
WORKDIR /app

ARG JAR_FILE=build/libs/InstaZoo-0.0.1-SNAPSHOT.jar

COPY --from=build /home/gradle/src/${JAR_FILE} /app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]