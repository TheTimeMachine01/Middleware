# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Install dependencies needed for the build (optional, alpine usually has basic tools)
RUN apk add --no-cache bash

# Copy the maven wrapper and pom.xml to download dependencies early
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Resolve dependencies (caching layer)
RUN ./mvnw dependency:go-offline -B

# Copy the source code and build the application
COPY src src
RUN ./mvnw package -DskipTests -B

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built jar from the build stage
# The jar name is typically Middleware-0.0.1-SNAPSHOT.jar based on pom.xml
COPY --from=build /workspace/target/Middleware-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Set environment variables with defaults
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# Health check (optional, but good for Docker/K8s)
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost:8080/api/healthz || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
