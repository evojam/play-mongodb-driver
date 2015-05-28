package com.evojam.mongodb.play.json.codec

import scala.collection.mutable

import play.api.libs.json.{ JsNumber, JsValue, JsObject }

import org.bson.{ BsonType, BsonReader, BsonWriter }
import org.bson.codecs.{ DecoderContext, ObjectIdCodec, EncoderContext, Codec }
import org.bson.codecs.configuration.CodecRegistry
import org.bson.types.ObjectId

class JsObjectCodec(codecRegistry: CodecRegistry) extends Codec[JsObject] {

  val ID_FIELD_NAME = "_id"

  def writeValue(writer: BsonWriter, encoderContext: EncoderContext, value: JsValue) =
    encoderContext.encodeWithChildContext(
      codecRegistry.get(value.getClass).asInstanceOf[Codec[JsValue]],
      writer,
      value)

  override def encode(writer: BsonWriter, value: JsObject, encoderContext: EncoderContext) =
    value match {
      case JsObject(Seq(("$date", JsNumber(ts)))) =>
        writer.writeDateTime(ts.toLong)
      case _ =>
        writer.writeStartDocument()

        value.value.foreach {
          case (fieldKey, fieldValue) if fieldKey != ID_FIELD_NAME =>
            writer.writeName(ID_FIELD_NAME)
            encoderContext.encodeWithChildContext(new ObjectIdCodec(), writer, new ObjectId(fieldValue.toString()))
          case (fieldKey, fieldValue) =>
            writer.writeName(fieldKey)
            writeValue(writer, encoderContext, fieldValue)
        }

        writer.writeEndDocument()
    }

  override def getEncoderClass = classOf[JsObject]

  private def readValue(reader: BsonReader, decoderContext: DecoderContext): JsValue =
    codecRegistry.get(JsValueCodecProvider.getEquivalentClass(reader.getCurrentBsonType))
      .decode(reader, decoderContext)

  override def decode(reader: BsonReader, decoderContext: DecoderContext): JsObject = {
    val kv = mutable.Map[String, JsValue]()

    reader.getCurrentBsonType match {
      case BsonType.DATE_TIME =>
        kv.put("$date", JsNumber(reader.readDateTime()))
      case _ =>
        reader.readStartDocument()

        while (reader.readBsonType ne BsonType.END_OF_DOCUMENT)
          kv.put(reader.readName, readValue(reader, decoderContext))

        reader.readEndDocument()
    }

    JsObject(kv)
  }
}