# ETAPA 1: Construcción (Usamos la imagen oficial de Maven con JDK 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos archivos de dependencias primero (Optimización de caché)
COPY pom.xml .
# Descargamos dependencias en modo offline para no descargarlas en cada build
RUN mvn dependency:go-offline

# Copiamos el código fuente
COPY src ./src

# Compilamos la aplicación (Saltando tests para que el deploy sea rápido)
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución (Imagen ligera de Java)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos el .jar cocinado en la etapa 1
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto estándar
EXPOSE 8080

# Comando de arranque optimizado
ENTRYPOINT ["java", "-jar", "app.jar"]