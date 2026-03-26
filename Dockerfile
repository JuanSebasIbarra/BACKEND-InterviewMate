# Stage 1: Build
FROM docker.io/library/eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Copy wrapper first for better layer caching
COPY gradlew .
COPY gradle ./gradle
RUN chmod +x gradlew

# Copy build files and pre-fetch dependencies
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon --stacktrace || true

# Copy source and build
COPY src ./src
RUN ./gradlew build -x test --no-daemon --stacktrace

# Stage 2: Run
FROM docker.io/library/eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]