# Etapa 1: Construcción
FROM docker.io/library/eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew build -x test --no-daemon

# Etapa 2: Ejecución
FROM docker.io/library/eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]