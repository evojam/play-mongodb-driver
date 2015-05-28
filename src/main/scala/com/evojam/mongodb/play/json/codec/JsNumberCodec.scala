package com.evojam.mongodb.play.json.codec

import play.api.libs.json.JsNumber

import org.bson.BsonType._
import org.bson.{ BsonReader, BsonWriter }
import org.bson.codecs.{ DecoderContext, EncoderContext, Codec }

class JsNumberCodec extends Codec[JsNumber] {

  override def encode(writer: BsonWriter, value: JsNumber, encoderContext: EncoderContext) =
    value.value match {
      case i if i.isValidInt =>
        writer.writeInt32(i.toInt)
      case l if l.isValidLong =>
        writer.writeInt64(l.toLong)
      case d =>
        writer.writeDouble(d.toDouble)
    }

  override def getEncoderClass = classOf[JsNumber]

  override def decode(reader: BsonReader, decoderContext: DecoderContext) =
    JsNumber(reader.getCurrentBsonType match {
      case INT64 => reader.readInt64()
      case INT32 => reader.readInt32()
      case DOUBLE => reader.readDouble()
      case TIMESTAMP => reader.readTimestamp().getTime
    })
}