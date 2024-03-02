package com.jambit.stream.detector

import com.jambit.stream.detector.MatchTypes.{MatchEvent, MatchState, Message}
import org.apache.flink.types.PojoTestUtils.assertSerializedAsPojoWithoutKryo
import org.scalatest.Inside
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class MatchTypesSpec extends AnyWordSpec with Matchers with Inside {

  "The match type" should {
    "Message should be a POJO according to Flink" in {
      assertSerializedAsPojoWithoutKryo(classOf[Message])
    }

    "MatchState should be a POJO according to Flink" in {
      assertSerializedAsPojoWithoutKryo(classOf[MatchState])
    }

    "MatchEvent should be a POJO according to Flink" in {
      assertSerializedAsPojoWithoutKryo(classOf[MatchEvent])
    }
  }
}
