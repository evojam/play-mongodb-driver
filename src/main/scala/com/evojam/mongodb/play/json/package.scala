package com.evojam.mongodb.play

import play.api.libs.json.{Json, Writes, JsValue, Reads}

import org.bson.codecs.configuration.CodecRegistries

import com.evojam.mongodb.client.MongoClientSettings
import com.evojam.mongodb.client.codec.{Writer, Reader}
import com.evojam.mongodb.play.json.codec._

package object json {
  val codecRegistry =
    CodecRegistries.fromRegistries(
      MongoClientSettings.Default.codecRegistry,
      CodecRegistries.fromProviders(new JsValueCodecProvider, new JsObjectCodecProvider, new JsArrayCodecProvider),
      CodecRegistries.fromCodecs(new JsBooleanCodec, new JsNumberCodec, new JsStringCodec))

  implicit lazy val jsValueCodec = new JsValueCodec(codecRegistry)
  implicit lazy val jsObjectCodec = new JsObjectCodec(codecRegistry)

  implicit def jsValueReader[T: Reads]: Reader[T] = new Reader[T] {
    override type R = JsValue
    override val codec = jsValueCodec

    override def read(doc: JsValue): T =
      doc.validate[T]
        .recoverTotal(e =>
        throw new Exception(s"Failed to decode json=$doc in reader ${this.getClass}  error=${e.toString}"))
  }

  implicit def jsValueWriter[T: Writes]: Writer[T] = new Writer[T] {
    override type R = JsValue
    override val codec = jsValueCodec

    override def write(doc: T): JsValue = Json.toJson(doc)
  }
}
