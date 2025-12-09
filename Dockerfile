# Build stage
FROM eclipse-temurin:21-jdk as build
WORKDIR /workspace
COPY . /workspace
RUN ./mvnw -B -DskipTests package

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY --from=build /workspace/target/Middleware-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

