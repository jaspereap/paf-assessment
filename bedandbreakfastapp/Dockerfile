FROM maven:3.9.5-eclipse-temurin-21 AS builder

WORKDIR /app
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src

RUN mvn package -Dmaven.test.skip=true

FROM maven:3.9.5-eclipse-temurin-21
WORKDIR /app
COPY --from=builder /app/target/assessment-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=3000
EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar