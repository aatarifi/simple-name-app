# === STAGE 1: Build Phase (Using Java 25) ===
FROM maven:3-eclipse-temurin-25-alpine AS build
WORKDIR /app

# Copy the configuration file and source code
COPY pom.xml .
COPY src ./src

# Compile the application into a JAR and skip tests
RUN mvn clean package -DskipTests

# === STAGE 2: Lightweight Runtime (Using Java 25) ===
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copy only the compiled JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 so the container can accept frontend traffic
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
