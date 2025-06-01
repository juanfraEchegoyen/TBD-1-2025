# Usa una imagen base de OpenJDK
FROM openjdk:17-jdk-slim

# Etiqueta del autor
LABEL authors="magik"

# Crea un directorio para la aplicación
WORKDIR /app

# Copia el JAR al contenedor
COPY target/GeoTaskApp-0.0.1-SNAPSHOT.jar /app/GeoTaskApp-0.0.1-SNAPSHOT.jar

# Define el puerto de ejecución en el contenedor
EXPOSE 8080

# Ejecuta la aplicación al iniciar el contenedor
ENTRYPOINT ["java", "-jar", "/app/GeoTaskApp-0.0.1-SNAPSHOT.jar"]
