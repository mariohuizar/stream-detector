# stream-detector
Sample stateful apache flink app with scala

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
