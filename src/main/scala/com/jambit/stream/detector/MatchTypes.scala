package com.jambit.stream.detector

object MatchTypes {
  // define types according to https://nightlies.apache.org/flink/flink-docs-release-1.17/docs/dev/datastream/fault-tolerance/serialization/types_serialization/#pojos

  class Message(
      var userId: String,
      var messageType: String,
      var messageValue: Int,
      var timeOfReceipt: Long
  ) {
    def this() = this("", "", -1, -1)
  }

  class MatchState(
      var startExperience: Int,
      var startTime: Long
  ) {
    def this() = this(-1, -1)

    override def toString: String = s"MatchState(" +
      s"startExperience=${startExperience.toString}, " +
      s"startTime=${startTime.toString}, " +
      s")"
  }

  class MatchEvent(var userId: String, var experienceWon: Int) {
    def this() = this("", -1)

    override def toString: String =
      s"MatchEvent(" +
        s"userId=$userId, " +
        s"experienceWon=${experienceWon.toString}" +
        s")"
  }
}
