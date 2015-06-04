package com.evojam

import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json

import org.specs2.mutable.Specification

import com.evojam.mongodb.client.MongoClientSettings
import com.evojam.mongodb.client.MongoClients
import com.evojam.mongodb.play.json.Codec
import com.evojam.mongodb.play.json.Codec.jsObjectCodec

class WeirdSpec extends Specification {

  "findOfType" should {
    "return list of JsObject for JsValue" in {
      val client = MongoClients.create(MongoClientSettings().codecRegistry(Codec.registry))

      val res = client.getDatabase("local")
        .collection("startup_log")
        .find()
        .collect[JsObject]

      res must not be empty.await(10)
      res must beAnInstanceOf[List[JsValue]].await(10)
    }

    "return list of JsObject for JsObject" in {
      val client = MongoClients.create(MongoClientSettings().codecRegistry(Codec.registry))

      val res = client.getDatabase("local")
        .collection("startup_log")
        .find()
        .collect[JsObject]

      res must not be empty.await(10)
      res must beAnInstanceOf[List[JsValue]].await(10)
    }
  }
}
