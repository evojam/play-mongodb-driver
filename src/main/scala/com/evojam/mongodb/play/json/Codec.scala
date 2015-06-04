package com.evojam.mongodb.play.json

import com.evojam.mongodb.client.MongoClientSettings
import com.evojam.mongodb.play.json.codec._
import org.bson.codecs.configuration.{ CodecRegistries, CodecRegistry }

object Codec {
  val registry =
    CodecRegistries.fromRegistries(
      MongoClientSettings.Default.codecRegistry,
      CodecRegistries.fromProviders(new JsValueCodecProvider, new JsObjectCodecProvider, new JsArrayCodecProvider),
      CodecRegistries.fromCodecs(new JsBooleanCodec, new JsNumberCodec, new JsStringCodec))

  implicit lazy val jsValueCodec = new JsValueCodec(registry)
  implicit lazy val jsObjectCodec = new JsObjectCodec(registry)
}
