package com.evojam

import play.api.libs.json.{ JsValue, JsObject, Json }

import com.evojam.mongodb.client.{ MongoClientSettings, MongoClients }
import com.evojam.mongodb.play.json.{ Codec, DateTimeFormatters }
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import com.evojam.mongodb.play.json.Codec._

class CodecsSpec extends Specification {

  val collection = MongoClients.create(
    MongoClientSettings().codecRegistry(Codec.registry))
    .database("codecs")
    .collection("codectest")

  val ts = (DateTime.now().getMillis / 1000).toInt

  sequential

  object Test extends AnyRef with DateTimeFormatters {
    val document = Json.obj(
      "_id" -> "1334",
      "create" -> DateTime.now(),
      "data" -> Json.obj("$binary" -> "YmluYXJ5ZGF0YWhlcmU=", "$type" -> "2"),
      "ts" -> Json.obj("$timestamp" -> Json.obj("t" -> ts, "i" -> 1)),
      "search" -> Json.obj("$regex" -> "/[a-z]+/", "$options" -> "i"),
      "dbpointer" -> Json.obj("$ref" -> "namespacename", "$id" -> "45plh3_6Sv6aneUWHL61Qw"),
      "field" -> Json.obj("$undefined" -> true),
      "longNum" -> Json.obj("$numberLong" -> "2134")
    )
  }

  "Codec tests" should {
    "encode and decode" in {

      val res =
        collection.findAndModify(Json.obj(), Json.obj("$set" -> Test.document))
          .returnFormer(false)
          .upsert(true)
          .collect[JsValue]
          .map(_.map(_.as[JsObject]))

      res must beSome(Test.document).await(10)
    }
  }
}
