package com.evojam.mongodb.play.json.codec

import scala.collection.mutable

import play.api.libs.json.{ JsValue, JsArray }

import org.bson.{ BsonType, BsonReader, BsonWriter }
import org.bson.codecs.{ DecoderContext, EncoderContext, Codec }
import org.bson.codecs.configuration.CodecRegistry

class JsArrayCodec(codecRegistry: CodecRegistry) extends Codec[JsArray] {

  def writeValue(writer: BsonWriter, encoderContext: EncoderContext, value: JsValue) =
    encoderContext.encodeWithChildContext(
      codecRegistry.get(value.getClass).asInstanceOf[Codec[JsValue]], // ??
      writer,
      value)

  override def encode(writer: BsonWriter, value: JsArray, encoderContext: EncoderContext) = {

    writer.writeStartArray()

    value.value.foreach(writeValue(writer, encoderContext, _))

    writer.writeEndArray()
  }

  override def getEncoderClass = classOf[JsArray]

  protected def readValue(reader: BsonReader, decoderContext: DecoderContext): JsValue =
    codecRegistry.get(JsValueCodecProvider.getEquivalentClass(reader.getCurrentBsonType))
      .decode(reader, decoderContext)

  override def decode(reader: BsonReader, decoderContext: DecoderContext) = {

    val items = mutable.ListBuffer.empty[JsValue]

    reader.readStartArray()

    while (reader.readBsonType ne BsonType.END_OF_DOCUMENT)
      items.append(readValue(reader, decoderContext))

    reader.readEndArray()

    JsArray(items)
  }
}