# Chinook Server Docker

This module provides a Docker container for the Chinook server application.

## Prerequisites

- Docker
- Docker Compose (optional)
- Java 25+ (for building the jlink image)

## Building and Deploying

### Quick Deploy (Recommended)

To build and deploy (restart the container with the updated image):

```bash
./gradlew :chinook-server-docker:dockerDeploy
```

This single command rebuilds the image and restarts the container. Use this whenever you modify configuration files in `chinook-server` (logback.xml, serialization-filter-patterns.txt, etc.).

### Build Only

To only build the Docker image without deploying:

```bash
./build-docker.sh
```

Or manually:

```bash
# Build the jlink image from chinook-server
./gradlew :chinook-server:jlink

# Prepare the jlink image for Docker
./gradlew :chinook-server-docker:prepareJlinkImage

# Build the Docker image
./gradlew :chinook-server-docker:dockerBuild
```

## Architecture

The Docker image uses a custom JRE created by jlink from the `chinook-server` module. This approach:
- Eliminates the need for a full JVM in the container
- Reduces image size significantly
- Includes only the required Java modules
- Uses the jlink launcher script with all configuration built-in

**Note**: This module requires the Linux jlink image from `chinook-server`. The build process specifically looks for the `-linux` jlink output. While you can build Docker images from Windows or macOS, the jlink image itself must be built for Linux (which is what Docker containers run). See [The Badass JLink Plugin](https://badass-jlink-plugin.beryx.org) for information on how to work around this.

## Running

### Using Docker Compose (recommended)

```bash
docker compose up -d
```

### Using Docker directly

```bash
./src/docker/run.sh
```

Or manually:

```bash
docker run --rm -d \
      --name chinook-server \
      -p 2223:2223 \
      -p 4445:4445 \
      -p 1098:1098 \
      -p 8088:8088 \
      chinook-server:latest
```

## Ports

- **1098**: RMI registry port
- **2223**: Server port (RMI)
- **4445**: Admin port (for server monitor)
- **8088**: HTTP servlet port

## Configuration

The server is configured via the jlink launcher script with:
- In-memory H2 database
- Default user: scott/tiger
- Admin user: scott/tiger
- SSL enabled with self-signed certificate
- Dual logging: console output and individual client log files

## Health Check

The container includes a health check that monitors the HTTP endpoint at `http://localhost:8088/`.

## Logging

The server uses dual logging:

1. **Console logs**: General server logs written to stdout/stderr (captured by Docker)
   ```bash
   docker logs chinook-server

   # Follow logs
   docker logs -f chinook-server
   ```

2. **Client-specific logs**: Individual client logs written to files in `/app/logs/`
   - `codion_server.log` - Default server log
   - `user@Chinook.log` - Individual client connection logs

   Access client logs via volume mount in `./logs/` directory, or directly from the container:
   ```bash
   # List log files
   docker exec chinook-server ls -la /app/logs/

   # View a specific log
   docker exec chinook-server cat /app/logs/scott@Chinook.log

   # Copy logs out of container
   docker cp chinook-server:/app/logs/ ./server-logs/
   ```

## Security

The container runs as a non-root user (uid 1000) for improved security.

## Troubleshooting

### No log files appearing in ./logs directory

If the container starts successfully but no log files appear in the `./logs/` directory on the host, it's likely a permissions issue. The server runs as user `chinook` (uid 1000) inside the container, so the mounted logs directory on the host must be writable by uid 1000.

Fix it with:
```bash
sudo chown -R 1000:1000 ./logs
```

Then restart the container:
```bash
docker compose restart
```

This issue typically occurs when the `./logs` directory is created by Docker with root ownership before the container starts.