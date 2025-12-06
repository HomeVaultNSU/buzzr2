# ===== BUILDER =====
FROM gradle:9.2.1-jdk25 AS builder

WORKDIR /app

COPY gradle/libs.versions.toml gradle/libs.versions.toml
COPY settings.gradle .
COPY build.gradle .

COPY agent ./agent
COPY service ./service

RUN gradle :service:bootJar --no-daemon

# ===== RUNTIME =====
FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY --from=builder /app/service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
