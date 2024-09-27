package com.jambit.stream.detector

import com.jambit.stream.detector.MatchTypes.Message
import org.apache.flink.api.common.typeinfo.Types
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
class MatchDetectorSpec extends AnyWordSpec with Matchers with BeforeAndAfter {

  "The match detector" should {

    "be able to detect a match" in {

      val harness = ProcessFunctionTestHarnesses.forKeyedProcessFunction(
        new MatchDetector,
        (m: Message) => m.userId,
        Types.STRING
      )

      val startMessage = new StreamRecord[Message](
        new Message(userId = "4711", messageType = "start", messageValue = 22, timeOfReceipt = 200)
      )
      val endMessage = new StreamRecord[Message](
        new Message(userId = "4711", messageType = "end", messageValue = 33, timeOfReceipt = 300)
      )

      harness.processElement(startMessage)
      harness.numKeyedStateEntries() shouldBe 1

      harness.processElement(endMessage)
      harness.numKeyedStateEntries() shouldBe 0

      harness.extractOutputValues().size() shouldBe 1
      val detectedMatch = harness.extractOutputValues().get(0)
      detectedMatch.userId shouldBe "4711"
      detectedMatch.experienceWon shouldBe 11
    }
  }
}
