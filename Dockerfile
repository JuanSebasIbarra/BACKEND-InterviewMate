# Etapa 1: Construcción de la aplicación con Gradle
FROM eclipse-temurin:17-jdk-jammy as builder

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar los archivos de Gradle para aprovechar la caché de capas de Docker
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# Descargar las dependencias
RUN ./gradlew dependencies

# Copiar el resto del código fuente y construir el proyecto
COPY src ./src
RUN ./gradlew build -x test

# Etapa 2: Creación de la imagen final de ejecución
FROM eclipse-temurin:17-jre-jammy

# Establecer el directorio de trabajo
WORKDIR /app

# Exponer el puerto en el que corre la aplicación (Railway lo detecta automáticamente)
EXPOSE 8080

# Copiar el archivo .jar construido desde la etapa anterior
# Gradle lo genera dentro de build/libs/
COPY --from=builder /app/build/libs/*.jar app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
