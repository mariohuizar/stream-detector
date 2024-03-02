package com.jambit.stream.detector

import com.jambit.stream.detector.MatchTypes.Message
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.slf4j.LoggerFactory

class MessageDeserializer extends DeserializationSchema[Message] {

  private val log = LoggerFactory.getLogger(this.getClass)

  override def deserialize(message: Array[Byte]): Message = {
    val string = new String(message)
    log.debug(s"Parsing message $string")
    val tokens = string.split(",")
    new Message(
      userId          = tokens(0),
      messageType   = tokens(1),
      messageValue  = tokens(2).toInt,
      timeOfReceipt = tokens(3).toLong
    )
  }

  override def isEndOfStream(nextElement: Message): Boolean = false

  override def getProducedType: TypeInformation[Message] = TypeInformation.of(classOf[Message])
}
