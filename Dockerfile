# Stage 1: Build the application
FROM gradle:8.11.1-jdk21 AS builder
WORKDIR /app

# Copy the project files
COPY --chown=gradle:gradle . .

# Build the application
RUN gradle build -x test --no-daemon

# Stage 2: Create a lightweight production image
FROM eclipse-temurin:21-jdk AS runtime
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]