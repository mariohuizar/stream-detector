package com.jambit.stream.detector

import com.jambit.stream.detector.MatchTypes.{MatchEvent, Message}
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.cep.pattern.Pattern
import org.apache.flink.cep.{CEP, PatternStream}
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.datastream.{DataStream, KeyedStream}
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

import java.time.Duration

object MatchDetectorApp {
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
    // exclude sources from watermarking which do not receive messages for more than 5s
    .withIdleness(Duration.ofSeconds(5))

  private val eventStream: DataStream[Message] = env
    .fromSource(kafkaSource, watermarkStrategy, "Kafka Source")
    .uid("kafka-source-id")

  // 1 ) Define a pattern: start -> end
  val startEndPattern: Pattern[Message, Message] = Pattern
    .begin[Message]("start")
    .where((value, _) => value.messageType == "start")
    .followedBy("end")
    .where((value, _) => value.messageType == "end")

  val keyedStream: KeyedStream[Message, String] = eventStream
    .keyBy(_.userId)

  val patternStream: PatternStream[Message] = CEP.pattern(keyedStream, startEndPattern)

  val matchEvents: DataStream[MatchEvent] =
    patternStream.select { map =>
      val startMsg = map.get("start").get(0)
      val endMsg   = map.get("end").get(0)
      new MatchEvent(
        userId        = startMsg.userId,
        experienceWon = endMsg.messageValue - startMsg.messageValue
      )
    }

  matchEvents.print().uid("print-sink-id")
  def detect(): Unit = env.execute()

  def main(args: Array[String]): Unit =
    detect()
}
