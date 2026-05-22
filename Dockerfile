# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies (this caches them to make future builds faster)
RUN mvn dependency:go-offline -B
# Copy source code and build the jar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application using the official Microsoft Playwright image
# This image contains Java and all the pre-installed web browsers!
FROM mcr.microsoft.com/playwright/java:v1.46.0-jammy
WORKDIR /app

# Copy the built jar from the 'build' stage
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 4000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
