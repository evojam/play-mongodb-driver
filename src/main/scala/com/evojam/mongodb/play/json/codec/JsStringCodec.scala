package com.evojam.mongodb.play.json.codec

import play.api.libs.json.JsString

import org.bson.codecs.{ Codec, DecoderContext, EncoderContext }
import org.bson.{ BsonReader, BsonType, BsonWriter }

class JsStringCodec extends Codec[JsString] {
  override def encode(writer: BsonWriter, value: JsString, encoderContext: EncoderContext) =
    writer.writeString(value.value)

  override def getEncoderClass = classOf[JsString]

  override def decode(reader: BsonReader, decoderContext: DecoderContext) = {
    reader.getCurrentBsonType match {
      case BsonType.OBJECT_ID =>
        JsString(reader.readObjectId().toHexString)
      case _ =>
        JsString(reader.readString())
    }
  }
}