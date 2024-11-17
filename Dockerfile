FROM maven:latest AS stage1
WORKDIR /auth_service
COPY pom.xml /auth_service
RUN mvn dependency:resolve
COPY . /auth_service
RUN mvn clean
RUN mvn package -DskipTests

FROM openjdk:21 AS final
COPY --from=stage1 /auth_service/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
