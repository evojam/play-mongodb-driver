package com.evojam.mongodb.play.json.codec

import play.api.Logger
import play.api.libs.json

import org.bson.codecs.Codec
import org.bson.codecs.configuration.{ CodecRegistry, CodecProvider }

class JsArrayCodecProvider extends CodecProvider {
  val logger = Logger(getClass)

  override def get[T](clazz: Class[T], registry: CodecRegistry): Codec[T] =
    if(clazz == classOf[json.JsArray]) {
      logger.debug(s"Will provide JsArrayCodec for class=$clazz")
      new JsArrayCodec(registry).asInstanceOf[Codec[T]]
    } else {
      logger.debug(s"Will not provide JsArrayCodec for class=$clazz")
      null
    }
}
