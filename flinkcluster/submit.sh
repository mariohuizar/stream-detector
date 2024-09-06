#!/bin/bash

DOCKER=${DOCKER:-docker}
if [[ -f /usr/libexec/java_home ]]; then
  JAVA_HOME="$(/usr/libexec/java_home -v 11)"
fi
java --version

cd "$(dirname "$0")"/..

echo "building..."
set -ex
sbt assembly
cp ./target/scala-2.13/stream-detector-assembly*-SNAPSHOT.jar flinkcluster/apps/stream-detector.jar

echo "submitting..."

$DOCKER exec flink-jobmanager chmod a+rwx /opt/flink-checkpoints
$DOCKER exec flink-jobmanager /opt/flink/bin/flink run \
  /opt/flink-apps/stream-detector.jar

