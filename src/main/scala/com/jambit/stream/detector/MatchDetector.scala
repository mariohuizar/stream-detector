package com.jambit.stream.detector

import com.jambit.stream.detector.MatchTypes.{ MatchEvent, MatchState, Message }
import org.apache.flink.api.common.state.{ ValueState, ValueStateDescriptor }
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory

class MatchDetector extends KeyedProcessFunction[String, Message, MatchEvent] {

  private val log = LoggerFactory.getLogger(this.getClass)

  private var storedMatchState: ValueState[MatchState] = _

  override def open(parameters: Configuration): Unit = {
    val storedMatchStateDescriptor =
      new ValueStateDescriptor[MatchState]("storedMatchState", classOf[MatchState])
    storedMatchStateDescriptor.setQueryable("matchState")
    storedMatchState = getRuntimeContext.getState(storedMatchStateDescriptor)
  }

  override def processElement(
      message: Message,
      ctx: KeyedProcessFunction[String, Message, MatchEvent]#Context,
      out: Collector[MatchEvent]
  ): Unit = {

    val maybeMatchState = Option(storedMatchState.value())

    log.debug(
      s"Processing message=${message.toString} for uid=${ctx.getCurrentKey} and based on stored state=${maybeMatchState.toString}"
    )

    (maybeMatchState, message.messageType) match {
      case (None, "start") =>
        val matchState = new MatchState(
          startExperience = message.messageValue,
          startTime       = message.timeOfReceipt,
          maxCoins        = 0
        )
        storedMatchState.update(matchState)
        log.debug(
          s"Stored match state for uid=${ctx.getCurrentKey}, state=${matchState.toString}"
        )
      case (Some(matchState), "end") =>
        val matchEvent =
          new MatchEvent(
            userId     = ctx.getCurrentKey,
            experience = message.messageValue - matchState.startExperience,
            maxCoins   = 0
          )
        log.debug(s"Detected match event ${matchEvent.toString}")
        out.collect(matchEvent)
        log.debug("Clearing state")
        storedMatchState.clear()
      case _ =>
        log.error(
          s"Unexpected state reached based on state=${maybeMatchState.toString} and message=${message.toString}"
        )
        storedMatchState.clear()
    }
  }
}
