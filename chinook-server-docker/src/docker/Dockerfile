# Dockerfile using jlink image from chinook-server module
# The jlink image is a self-contained custom JRE with the application
# Using Debian slim for glibc compatibility with jlink native libraries
FROM debian:bookworm-slim

# Install only curl for health checks (glibc is already present)
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy the entire jlink runtime
# Expects the jlink output to be copied into build context as 'jlink-image/'
COPY jlink-image/ /app/

# Create a non-root user to run the application
RUN groupadd -g 1000 chinook && \
    useradd -r -u 1000 -g chinook chinook && \
    mkdir -p /app/logs && \
    chown -R chinook:chinook /app

# Switch to non-root user
USER chinook

# Expose the necessary ports
# 1098 - RMI registry port
# 2223 - Server port (RMI)
# 4445 - Admin port
# 8088 - HTTP servlet port
EXPOSE 1098 2223 4445 8088

# Health check using HTTP endpoint
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
    CMD curl -s http://localhost:8088/ > /dev/null || exit 1

# Override JVM args for Docker environment (0.0.0.0 binding)
ENV JAVA_TOOL_OPTIONS="-Djava.rmi.server.hostname=0.0.0.0"

# Run the server using the jlink launcher script
ENTRYPOINT ["/app/bin/chinook-server"]
