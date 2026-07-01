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

# ─── Runtime Stage ────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# C4 Fix: Create non-root user for security (never run as root in container)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Set ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8000

# C4 Fix: JVM tuning flags for containers + security settings
ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseContainerSupport", \
  "-XX:+UseG1GC", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.backgroundpreinitializer.ignore=true", \
  "-jar", "/app/app.jar"]

# C4 Fix: Docker-native health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8000/actuator/health || exit 1
