# Etapa 1: Maven compila con JDK 21 y genera el JAR dentro de Docker.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

# Etapa 2: Java 21 ejecuta el JAR; Maven no se incluye en la imagen final.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
