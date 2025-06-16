FROM openjdk:23-jdk AS build
ADD build/install/chinook-server /home/chinook-server
RUN microdnf install findutils
EXPOSE 1098 2223 4445 8088
ENTRYPOINT ["/home/chinook-server/bin/chinook-server"]