FROM maven:3.9.4-eclipse-temurin-21-alpine AS build-stage
WORKDIR /app

COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline

COPY ./src ./src
RUN mvn --batch-mode clean package -DskipTests


FROM openjdk:21-slim AS run-stage

RUN groupadd -g 1000 app && useradd app -u 1000 -g 1000 \
&& mkdir /app && chown app /app && chgrp app /app

USER app
WORKDIR /app

COPY --chown=app:app --from=build-stage /app/target/msg-relay-*.jar /app/msg-relay.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar msg-relay.jar"]