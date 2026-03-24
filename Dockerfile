# Etapa 1: Construcción de la aplicación con Maven
FROM eclipse-temurin:17-jdk-jammy as builder

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo pom.xml y descargar dependencias para aprovechar el cache de Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el resto del código fuente y construir el proyecto
COPY src ./src
RUN mvn package -DskipTests

# Etapa 2: Creación de la imagen final de ejecución
FROM eclipse-temurin:17-jre-jammy

# Establecer el directorio de trabajo
WORKDIR /app

# Exponer el puerto en el que corre la aplicación Spring Boot (por defecto 8080)
EXPOSE 8080

# Copiar el archivo .jar construido desde la etapa anterior
# El nombre del JAR puede variar, ajusta "InterviewMate-0.0.1-SNAPSHOT.jar" si es diferente.
COPY --from=builder /app/target/InterviewMate-0.0.1-SNAPSHOT.jar app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
