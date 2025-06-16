# Use OpenJDK base image (compatible with jlink output)
FROM eclipse-temurin:21-jre-alpine

# Install required packages
RUN apk add --no-cache \
    wget \
    net-tools

# Set working directory
WORKDIR /app

# Copy the JAR file and resources
COPY build/libs/*-all.jar /app/chinook-server.jar
COPY src/main/resources/ /app/

# Create a non-root user to run the application
RUN addgroup -g 1000 chinook && \
    adduser -D -u 1000 -G chinook chinook && \
    chown -R chinook:chinook /app

# Switch to non-root user
USER chinook

# Expose the necessary ports
# 1098 - RMI registry port
# 2223 - Server port (RMI)  
# 4445 - Admin port
# 8088 - HTTP servlet port
EXPOSE 1098 2223 4445 8088

# Health check using netstat to check if ports are listening
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD netstat -ln | grep -q ':2223.*LISTEN' || exit 1

# Run the server
ENTRYPOINT ["java", "-jar", "/app/chinook-server.jar"]