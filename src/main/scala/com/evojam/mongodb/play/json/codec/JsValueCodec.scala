package com.evojam.mongodb.play.json.codec

import play.api.libs.json.{ JsObject, JsNull, JsValue }

import org.bson.{ BsonType, BsonReader, BsonWriter }
import org.bson.codecs.{ DecoderContext, EncoderContext, Codec }
import org.bson.codecs.configuration.CodecRegistry

class JsValueCodec(codecRegistry: CodecRegistry) extends Codec[JsValue] {

  override def encode(writer: BsonWriter, value: JsValue, encoderContext: EncoderContext) =
    value match {
      case JsNull =>
        writer.writeNull()
      case _ =>
        encoderContext.encodeWithChildContext(
          codecRegistry.get(value.getClass).asInstanceOf[Codec[JsValue]],
          writer,
          value)
    }

  override def getEncoderClass = classOf[JsValue]

  override def decode(reader: BsonReader, decoderContext: DecoderContext) =
    if(reader.getCurrentBsonType == BsonType.NULL) {
      JsNull
    } else {
      val bsonType = Option(reader.getCurrentBsonType).getOrElse(reader.readBsonType())

      codecRegistry.get(JsValueCodecProvider.getEquivalentClass(bsonType))
        .decode(reader, decoderContext)
    }
}