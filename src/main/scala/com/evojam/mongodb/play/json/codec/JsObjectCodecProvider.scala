package com.evojam.mongodb.play.json.codec

import play.api.Logger
import play.api.libs.json

import org.bson.codecs.Codec
import org.bson.codecs.configuration.{ CodecRegistry, CodecProvider }

class JsObjectCodecProvider extends CodecProvider {
  val logger = Logger(getClass)

  override def get[T](clazz: Class[T], registry: CodecRegistry): Codec[T] =
    if(clazz == classOf[json.JsObject]) {
      logger.debug(s"Will provide JsObjectCodec for class=$clazz")
      new JsObjectCodec(registry).asInstanceOf[Codec[T]]
    } else {
      val r = json.JsObject.getClass
      logger.debug(s"Will not provide JsObjectCodec for $clazz required $r")
      null
    }
}