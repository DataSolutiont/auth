# Этап сборки
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /auth_service
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Этап выполнения
FROM eclipse-temurin:21-jre
COPY --from=builder /auth_service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]