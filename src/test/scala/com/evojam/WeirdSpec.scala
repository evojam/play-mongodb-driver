package com.evojam

import com.evojam.mongodb.client.{ MongoClientSettings, MongoClients }
import com.evojam.mongodb.play.json.Codec
import org.bson.BsonDocument
import org.specs2.mutable.Specification

import play.api.libs.json.{ JsObject, JsValue }

class WeirdSpec extends Specification {

  "findOfType" should {
    "return list of JsObject for JsValue" in {
      val client = MongoClients.create(MongoClientSettings().codecRegistry(Codec.registry))

      val res = client.getDatabase("local").getCollection[JsValue]("startup_log")
        .findOfType[JsValue](new BsonDocument())
        .collect()

      res must not be empty.await(10)
      res must beAnInstanceOf[List[JsValue]].await(10)
    }

    "return list of JsObject for JsObject" in {
      val client = MongoClients.create(MongoClientSettings().codecRegistry(Codec.registry))

      val res = client.getDatabase("local").getCollection[JsObject]("startup_log")
        .findOfType[JsObject](new BsonDocument())
        .collect()

      res must not be empty.await(10)
      res must beAnInstanceOf[List[JsValue]].await(10)
    }
  }
}
