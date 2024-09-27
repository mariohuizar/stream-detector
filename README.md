# Stream match detector

This project is an Apache Flink application written in Scala that processes streaming data from Kafka
to detect events based on incoming messages. It uses Flink’s keyed process functions and 
the complex event processing (CEP) library to identify and output match events.

It receives a stream of Messages via kafka as a String with the format:
```
<USER_ID>,[start|end],<EXPERIENCE>,<TIMESTAMP>
```

They represent if a specific user started or ended a gaming session and the current experience.

The application task is to generate an event when the user id generates a matching pair i.e.
```
123, start, 0, 1 # User 123 started a gaming session with 0 experience at timestamp 1
123, end, 5, 2   # User 123 ended a gaming session with 5 experience at timestamp 2
```

Generates a MatchEvent
```
123, 5  # User 123 won 5 experience
```

## Run in a local cluster


```shell
# Go to the flinkcluster directory.
cd flinkcluster
# start the flinkcluster docker compose.
docker compose up -d
# wait until flink cluster is up and running
# check at http://localhost:8081/
# create the test kafka topic
./kafkaCreateTopic.sh
# submit the jar flink app into the cluster
./submit.sh
# start generating events
./kakfaWriter.sh
# Check the logs for inspecting the generated events
docker logs flink-taskmanager-a
docker logs flink-taskmanager-b
```
