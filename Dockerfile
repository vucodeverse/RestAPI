FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Make mvnw executable and download dependencies
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline

# Copy the project source
COPY src ./src
# Build the application
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the built jar
COPY --from=builder /app/target/*.jar app.jar
# Expose the default port
EXPOSE 8000
# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
