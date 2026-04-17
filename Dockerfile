# syntax=docker/dockerfile:1.7

############################################
# Stage 1 — Build
############################################
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN chmod +x mvnw

RUN ./mvnw -B -DskipTests package

COPY src src

RUN ./mvnw -B -DskipTests package


############################################
# Stage 2 — Runtime
############################################
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN useradd -ms /bin/bash spring

COPY --from=builder /build/target/*.jar app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]