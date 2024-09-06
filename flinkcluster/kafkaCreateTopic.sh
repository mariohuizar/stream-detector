#!/bin/bash

DOCKER=${DOCKER:-docker}

$DOCKER exec -it kafka-a kafka-topics --create --replication-factor 1 --partitions 1 --topic test --bootstrap-server localhost:9092

