# Stage 1: Build the application
FROM gradle:8.10.2-jdk23 AS builder

# Set working directory
WORKDIR /app

# Copy gradle files first to cache dependencies
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# Download dependencies
RUN gradle dependencies --no-daemon

# Copy source code
COPY src src

# Build the application
RUN gradle build --no-daemon -x test

# Stage 2: Run the application
FROM eclipse-temurin:21.0.5_11-jre-jammy

# Add a non-root user to run the app
RUN useradd -m javauser

# Create directory for app and set ownership
WORKDIR /app
RUN chown javauser:javauser /app

# Set the active user
USER javauser

# Copy the built artifact from builder stage
COPY --from=builder --chown=javauser:javauser /app/build/libs/*.jar app.jar

# Configure JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+OptimizeStringConcat \
               -XX:+UseStringDeduplication \
               -Djava.security.egd=file:/dev/./urandom"

# Expose the application port
EXPOSE 8080

# Set the entrypoint
ENTRYPOINT ["bash", "-c", "java $JAVA_OPTS -jar app.jar"]