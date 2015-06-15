package com.evojam.mongodb.play.json.codec


import java.util.Base64

import scala.collection.mutable

import play.api.libs.json._

import org.bson._
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.{ Codec, DecoderContext, EncoderContext, ObjectIdCodec }
import org.bson.types.ObjectId

import scala.util.control.Exception.catching

class JsObjectCodec(codecRegistry: CodecRegistry) extends Codec[JsObject] {

  val ID_FIELD_NAME = "_id"
  val RadixHex = 16

  def writeValue(writer: BsonWriter, encoderContext: EncoderContext, value: JsValue) =
    encoderContext.encodeWithChildContext(
      codecRegistry.get(value.getClass).asInstanceOf[Codec[JsValue]],
      writer,
      value)

  private def isValidLong(in: String) =
    catching(classOf[IllegalArgumentException]).opt(in.toLong).isDefined


  // scalastyle:off cyclomatic.complexity
  // scalastyle:off method.length
  // PoC implementation, unsafe for corner cases
  override def encode(writer: BsonWriter, input: JsObject, encoderContext: EncoderContext) =
    input.value.toSeq match {
      case Seq(("$date", JsNumber(ts))) if ts.isValidLong =>
        writer.writeDateTime(ts.toLong)

      case Seq(("$binary", JsString(data)), ("$type", JsString(dataType))) =>
        val decoder = Base64.getDecoder
        writer.writeBinaryData(new BsonBinary(Integer.parseInt(dataType, RadixHex).toByte, decoder.decode(data)))

      case Seq(("$timestamp", JsObject(in))) =>
        in.toSeq match {
          case Seq(("t", JsNumber(ts)), ("i", JsNumber(inc))) if ts.isValidInt && inc.isValidInt =>
            writer.writeTimestamp(new BsonTimestamp(ts.toInt, inc.toInt))
          case _ =>
            throw new Exception("Timestamp json invalid") // TODO: Add custom exeception and valid message
        }

      case Seq(("$regex", JsString(regex)), ("$options", JsString(options))) =>
        writer.writeRegularExpression(new BsonRegularExpression(regex, options))

      case Seq(("$oid", JsString(hex))) if ObjectId.isValid(hex) =>
        writer.writeObjectId(new ObjectId(hex))

      case Seq(("$ref", JsString(ref)), ("$id", JsString(id))) if ObjectId.isValid(id) =>
        writer.writeDBPointer(new BsonDbPointer(ref, new ObjectId(id)))

      case Seq(("$undefined", JsBoolean(true))) =>
        writer.writeUndefined()

      case Seq(("$minKey", JsNumber(_))) =>
        writer.writeMinKey()

      case Seq(("$maxKey", JsNumber(_))) =>
        writer.writeMaxKey()

      case Seq(("$numberLong", JsString(value))) if isValidLong(value) =>
        writer.writeInt64(value.toLong)

      case _ =>
        writer.writeStartDocument()

        input.value.foreach {
          case (fieldKey, fieldValue) if fieldKey == ID_FIELD_NAME && ObjectId.isValid(fieldValue.toString()) =>
            writer.writeName(ID_FIELD_NAME)
            encoderContext.encodeWithChildContext(new ObjectIdCodec(), writer, new ObjectId(fieldValue.toString()))
          case (fieldKey, fieldValue) =>
            writer.writeName(fieldKey)
            writeValue(writer, encoderContext, fieldValue)
        }

        writer.writeEndDocument()
    }

  // scalastyle:on cyclomatic.complexity
  // scalastyle:on method.length

  override def getEncoderClass = classOf[JsObject]

  private def readValue(reader: BsonReader, decoderContext: DecoderContext): JsValue =
    codecRegistry.get(JsValueCodecProvider.getEquivalentClass(reader.getCurrentBsonType))
      .decode(reader, decoderContext)

  // scalastyle:off cyclomatic.complexity
  // scalastyle:off method.length
  override def decode(reader: BsonReader, decoderContext: DecoderContext): JsObject = {
    val kv = mutable.Map[String, JsValue]()

    reader.getCurrentBsonType match {
      case BsonType.BINARY =>
        val encoder = Base64.getEncoder
        val binary = reader.readBinaryData()
        kv.put("$binary", JsString(encoder.encodeToString(binary.getData)))
        kv.put("$type", JsString(binary.getType.toInt.toHexString))

      case BsonType.DATE_TIME =>
        kv.put("$date", JsNumber(reader.readDateTime()))

      case BsonType.TIMESTAMP =>
        val ts = reader.readTimestamp()
        kv.put("$timestamp", Json.obj("t" -> ts.getTime, "i" -> ts.getInc))

      case BsonType.REGULAR_EXPRESSION =>
        val regex = reader.readRegularExpression()
        kv.put("$regex", JsString(regex.getPattern))
        kv.put("$options", JsString(regex.getOptions))

      case BsonType.OBJECT_ID =>
        kv.put("$oid", JsString(reader.readObjectId().toHexString))

      case BsonType.DB_POINTER =>
        val db = reader.readDBPointer()
        // FIXME: http://docs.mongodb.org/manual/reference/mongodb-extended-json/#db-reference - WTF id type
        kv.put("$ref", JsString(db.getNamespace))
        kv.put("$id", JsString(db.getId.toHexString))

      case BsonType.UNDEFINED =>
        reader.readUndefined()
        kv.put("$undefined", JsBoolean(true))

      case BsonType.MIN_KEY =>
        reader.readMinKey()
        kv.put("$minKey", JsNumber(1))

      case BsonType.MAX_KEY =>
        reader.readMaxKey()
        kv.put("$maxKey", JsNumber(1))

      case BsonType.INT64 =>
        // FIXME: Implemented but not used as JsNumber accepts Long integers - should be available for full compliance
        kv.put("$numberLong", JsString(reader.readInt64().toString))

      case _ =>
        reader.readStartDocument()

        while (reader.readBsonType ne BsonType.END_OF_DOCUMENT)
          kv.put(reader.readName, readValue(reader, decoderContext))

        reader.readEndDocument()
    }

    JsObject(kv)
  }

  // scalastyle:on cyclomatic.complexity
  // scalastyle:on method.length
}