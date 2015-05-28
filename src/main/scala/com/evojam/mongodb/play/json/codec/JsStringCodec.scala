package com.evojam.mongodb.play.json.codec

import play.api.libs.json.JsString

import org.bson.{ BsonReader, BsonWriter }
import org.bson.codecs.{ DecoderContext, EncoderContext, Codec }

class JsStringCodec extends Codec[JsString] {
  override def encode(writer: BsonWriter, value: JsString, encoderContext: EncoderContext) =
    writer.writeString(value.value)

  override def getEncoderClass = classOf[JsString]

  override def decode(reader: BsonReader, decoderContext: DecoderContext) =
    JsString(reader.readString())
}