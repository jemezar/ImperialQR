# Etapa 1: compilación
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# Etapa 2: ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S imperial && adduser -S imperial -G imperial
COPY --from=build /app/target/imperial-qr-*.jar app.jar
USER imperial
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
