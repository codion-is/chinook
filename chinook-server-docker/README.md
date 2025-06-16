# Chinook Server Docker

This module provides a Docker container for the Chinook server application.

## Prerequisites

- Docker
- Docker Compose (optional)
- Java 21+ (for building)

## Building

To build the Docker image, run:

```bash
./build-docker.sh
```

Or manually:

```bash
# Build the jlink image
./gradlew :chinook-server-docker:jlink

# Build the Docker image
./gradlew :chinook-server-docker:dockerBuild
```

## Running

### Using Docker Compose

```bash
docker-compose up -d
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

The server is configured with:
- In-memory H2 database
- Default user: scott/tiger
- Admin user: scott/tiger
- SSL enabled with self-signed certificate
- Client logging disabled by default

## Health Check

The container includes a health check that monitors the HTTP endpoint at `http://localhost:8088/health`.

## Security

The container runs as a non-root user (uid 1000) for improved security.