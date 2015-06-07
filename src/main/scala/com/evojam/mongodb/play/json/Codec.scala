package com.evojam.mongodb.play.json

import play.api.libs.json._

import org.bson.codecs.configuration.CodecRegistries

import com.evojam.mongodb.client.MongoClientSettings
import com.evojam.mongodb.client.codec.Reader
import com.evojam.mongodb.client.codec.Writer
import com.evojam.mongodb.play.json.codec._

object Codec {
  val registry =
    CodecRegistries.fromRegistries(
      MongoClientSettings.Default.codecRegistry,
      CodecRegistries.fromProviders(new JsValueCodecProvider, new JsObjectCodecProvider, new JsArrayCodecProvider),
      CodecRegistries.fromCodecs(new JsBooleanCodec, new JsNumberCodec, new JsStringCodec))

  implicit lazy val jsValueCodec = new JsValueCodec(registry)
  implicit lazy val jsObjectCodec = new JsObjectCodec(registry)

  implicit def jsValueReader[T: Reads]: Reader[T] = new Reader[T] {
    override type R = JsValue
    override val codec = jsValueCodec
    override def read(doc: JsValue): T = Json.fromJson(doc).get
  }

  implicit def jsValueWriter[T: Writes]: Writer[T] = new Writer[T] {
    override type R = JsValue
    override val codec = jsValueCodec
    override def write(doc: T): JsValue = Json.toJson(doc)
  }
}
