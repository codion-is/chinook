# Use OpenJDK base image (compatible with jlink output)
FROM eclipse-temurin:21-jre-alpine

# Install required packages
RUN apk add --no-cache \
    wget \
    netstat-nat

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
ENTRYPOINT ["java", \
    "-Xmx512m", \
    "-Dlogback.configurationFile=logback.xml", \
    "-Djava.rmi.server.hostname=0.0.0.0", \
    "-Dcodion.server.registryPort=1098", \
    "-Djava.rmi.server.randomIDs=true", \
    "-Djava.rmi.server.useCodebaseOnly=true", \
    "-Dcodion.server.objectInputFilterFactoryClassName=is.codion.common.rmi.server.SerializationFilterFactory", \
    "-Dcodion.server.serialization.filter.patternFile=classpath:serialization-filter-patterns.txt", \
    "-Dcodion.server.classpathKeyStore=keystore.jks", \
    "-Djavax.net.ssl.keyStorePassword=crappypass", \
    "-Dcodion.server.port=2223", \
    "-Dcodion.server.auxiliaryServerFactoryClassNames=is.codion.framework.servlet.EntityServiceFactory", \
    "-Dcodion.server.http.secure=false", \
    "-Dcodion.server.http.port=8088", \
    "-Dcodion.server.http.useVirtualThreads=true", \
    "-Dcodion.server.admin.port=4445", \
    "-Dcodion.server.admin.user=scott:tiger", \
    "-Dcodion.db.url=jdbc:h2:mem:h2db", \
    "-Dcodion.db.initScripts=classpath:create_schema.sql", \
    "-Dcodion.db.countQueries=true", \
    "-Dcodion.server.connectionPoolUsers=scott:tiger", \
    "-Dcodion.server.clientLogging=false", \
    "-jar", "/app/chinook-server.jar"]