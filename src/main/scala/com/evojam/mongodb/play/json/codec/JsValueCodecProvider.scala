package com.evojam.mongodb.play.json.codec

import play.api.Logger
import play.api.libs.json

import org.bson.BsonType
import org.bson.codecs.Codec
import org.bson.codecs.configuration.{ CodecRegistry, CodecProvider }

class JsValueCodecProvider extends CodecProvider {
  val logger = Logger(getClass)

  override def get[T](clazz: Class[T], registry: CodecRegistry): Codec[T] =
    if(clazz == classOf[json.JsValue]) {
      logger.debug(s"Will provide JsValueCodec for class=$clazz")
      new JsValueCodec(registry).asInstanceOf[Codec[T]]
    } else {
      logger.debug(s"Will not provide JsValueCodec for class=$clazz")
      null
    }
}

object JsValueCodecProvider {

  val classMap: Map[BsonType, Class[_ <: json.JsValue]] = Map(
    BsonType.DOUBLE -> classOf[json.JsNumber],
    BsonType.STRING -> classOf[json.JsString],
    BsonType.DOCUMENT -> classOf[json.JsObject],
    BsonType.ARRAY -> classOf[json.JsArray],
    BsonType.OBJECT_ID -> classOf[json.JsObject],
    BsonType.BOOLEAN -> classOf[json.JsBoolean],
    BsonType.DATE_TIME -> classOf[json.JsObject],
    BsonType.INT32 -> classOf[json.JsNumber],
    BsonType.TIMESTAMP -> classOf[json.JsObject],
    BsonType.INT64 -> classOf[json.JsObject],
    BsonType.BINARY -> classOf[json.JsObject],
    BsonType.REGULAR_EXPRESSION -> classOf[json.JsObject],
    BsonType.MAX_KEY -> classOf[json.JsObject],
    BsonType.MIN_KEY -> classOf[json.JsObject],
    BsonType.UNDEFINED -> classOf[json.JsObject])

  def getEquivalentClass(sourceClass: BsonType): Class[_ <: json.JsValue] =
    classMap.getOrElse(sourceClass, null)
}