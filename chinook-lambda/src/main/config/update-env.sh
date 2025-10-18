#\!/bin/bash
aws lambda update-function-configuration \
  --function-name chinook-lambda \
  --environment Variables="{\"JAVA_TOOL_OPTIONS\":\"-Dcodion.db.url=jdbc:h2:mem:h2db -Dcodion.db.initScripts=classpath:create_schema.sql -Dcodion.db.countQueries=true -Dcodion.server.connectionPoolUsers=scott:tiger -Dcodion.server.objectInputFilterFactory=is.codion.common.rmi.server.SerializationFilterFactory -Dcodion.server.serialization.filter.patternFile=classpath:serialization-filter-patterns.txt -Dcodion.server.idleConnectionTimeout=600000\"}"