package com.jambit.stream.detector

import com.jambit.stream.detector.MatchTypes.Message
import org.apache.flink.api.common.eventtime.{ SerializableTimestampAssigner, WatermarkStrategy }
import org.apache.flink.cep.CEP
import org.apache.flink.cep.functions.PatternProcessFunction.Context
import org.apache.flink.cep.pattern.Pattern
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory

import java.time.Duration

object MatchDetectorApp {
  private val log = LoggerFactory.getLogger(this.getClass)

  private val executionConfiguration = new Configuration()

  private val env = StreamExecutionEnvironment.getExecutionEnvironment(executionConfiguration)
  env.getConfig.setAutoWatermarkInterval(1_000)

  env.enableCheckpointing(5_000)

  private val kafkaSource = KafkaSource
    .builder[Message]
    .setBootstrapServers("kafka-a:29092")
    .setTopics("test")
    .setGroupId("match-detector")
    .setStartingOffsets(OffsetsInitializer.earliest)
    .setValueOnlyDeserializer(new MessageDeserializer)
    .build

  private val watermarkStrategy = WatermarkStrategy
    .forBoundedOutOfOrderness(Duration.ofSeconds(1))
    .withTimestampAssigner(new SerializableTimestampAssigner[Message] {
      override def extractTimestamp(element: Message, recordTimestamp: Long): Long =
        element.timeOfReceipt
    })
    // exclude sources form watermarking which do not receive messages for more than 5s
    .withIdleness(Duration.ofSeconds(5))

  private val eventStream: DataStream[Message] = env
    .fromSource(kafkaSource, watermarkStrategy, "Kafka Source")
    .uid("kafka-source-id")

  def detect(): Unit = {
    val anyMessage = Pattern.begin[Message]("message")

    val orderedStream = CEP
      .pattern(eventStream, anyMessage)
      .process {
        (
            pattern: java.util.Map[String, java.util.List[Message]],
            _: Context,
            out: Collector[Message]
        ) =>
          out.collect(pattern.get("message").get(0))
      }
      .uid("cep-operator-id")

    val allMatchEventsPerUserId = orderedStream
      .keyBy((m: Message) => m.userId)
      .process(new MatchDetector)
      .uid("keyed-process-function-id")

    allMatchEventsPerUserId
      .print()
      .uid("print-sink-id")

    env.execute()
  }

  def main(args: Array[String]): Unit =
    detect()
}
