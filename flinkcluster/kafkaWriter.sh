#!/bin/bash

# Script to produce user experienceWon messages to Kafka in a loop

DOCKER=${DOCKER:-docker}
SLEEP_INTERVAL=1
USER_ID=id123
EXPERIENCE=1
TIMESTAMP=100000

trap "echo 'Script terminated'; exit" SIGINT SIGTERM

while true; do
    echo "$USER_ID,start,$EXPERIENCE,$TIMESTAMP"
    sleep $SLEEP_INTERVAL

    ((TIMESTAMP += 1000))
    EXPERIENCE_INCREASE=$(shuf -i 0-100 -n 1)
    EXPERIENCE=$((EXPERIENCE + EXPERIENCE_INCREASE))

    echo "$USER_ID,end,$EXPERIENCE,$TIMESTAMP"
    sleep $SLEEP_INTERVAL

    ((TIMESTAMP += 1000))
done | $DOCKER exec -i kafka-a kafka-console-producer --broker-list localhost:9092 --topic test