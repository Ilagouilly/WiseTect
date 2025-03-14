# Use official OpenJDK image
FROM eclipse-temurin:23-jdk-alpine

# Install Maven
RUN apk add --no-cache maven

# Set working directory
WORKDIR /app

# Copy entire project
COPY . .

# Build the entire project
RUN mvn clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/architecture-service/target/architecture-service-1.0.0-SNAPSHOT-exec.jar"]
