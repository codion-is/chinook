#!/bin/bash

# Run the Chinook server in Docker
docker run --rm -d           \
      --name chinook-server  \
      -p 2223:2223           \
      -p 4445:4445           \
      -p 1098:1098           \
      -p 8088:8088           \
      chinook-server:latest