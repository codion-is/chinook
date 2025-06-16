docker run --rm -d           \
      --name chinook-server  \
      -p 2223:2223           \
      -p 4445:4445           \
      -p 1098:1098           \
      -p 8088:8088           \
      -e ENVIRONMENT=docker  \
      chinook-server:latest