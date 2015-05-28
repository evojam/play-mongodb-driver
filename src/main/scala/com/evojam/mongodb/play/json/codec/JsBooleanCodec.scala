package com.evojam.mongodb.play.json.codec

import play.api.libs.json.JsBoolean

import org.bson.{ BsonReader, BsonWriter }
import org.bson.codecs.{ DecoderContext, EncoderContext, Codec }

class JsBooleanCodec extends Codec[JsBoolean] {

  override def encode(writer: BsonWriter, value: JsBoolean, encoderContext: EncoderContext) =
    writer.writeBoolean(value.value)

  override def getEncoderClass = classOf[JsBoolean]

  override def decode(reader: BsonReader, decoderContext: DecoderContext) =
    JsBoolean(reader.readBoolean())
}