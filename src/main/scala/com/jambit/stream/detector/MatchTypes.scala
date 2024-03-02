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
      var startTime: Long,
      var maxCoins: Int
  ) {
    def this() = this(-1, -1, -1)

    override def toString: String = s"MatchState(" +
      s"startMileage=${startExperience.toString}, " +
      s"startTime=${startTime.toString}, " +
      s"maxCoins=${maxCoins.toString}" +
      s")"
  }

  class MatchEvent(var userId: String, var experience: Int, var maxCoins: Int) {
    def this() = this("", -1, -1)

    override def toString: String =
      s"MatchEvent(" +
        s"userId=$userId, " +
        s"experience=${experience.toString}, " +
        s"maxCoins=${maxCoins.toString}" +
        s")"
  }
}
