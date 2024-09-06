# stream-detector
Sample stateful apache flink app with scala

## Run in a local cluster

Go to the flinkcluster directory.

```shell
# start the flinkcluster docker compose.
docker compose up -d
# wait until flink cluster is up and running
# execute the kafkaCreateTopic script
./kafkaCreateTopic.sh
# submit the jar flink app into the cluster
./submit.sh
# start generating events
./kakfaCreateTopic.sh
# Check the logs for inspecting the generated events
docker logs flink-taskmanager-a
docker logs flink-taskmanager-b
```
