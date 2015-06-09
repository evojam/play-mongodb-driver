package com.evojam

import play.api.libs.json.JsObject
import play.api.libs.json.Json

import org.specs2.mutable.After
import org.specs2.mutable.Specification

import com.evojam.mongodb.client.MongoClientSettings
import com.evojam.mongodb.client.MongoClients
import com.evojam.mongodb.play.json.Codec
import com.evojam.mongodb.play.json.Codec._

class QueryJsonSpec extends Specification with After {
  val collection = MongoClients.create(
    MongoClientSettings().codecRegistry(Codec.registry))
    .database("foodb")
    .collection("queryjsonspec")

  val collectionContent = List(
    Json.obj("_id" -> "first"),
    Json.obj("_id" -> "second"))

  sequential

  "MongoClient" should {
    "insert list of JsObject objects to a collection" in {
      val count = collection
        .insertAll(collectionContent)
        .flatMap(_ => collection.count)

      count must beEqualTo(collectionContent.size).await
    }

    "query JsObjects from a collection" in {
      val result = collection
        .find()
        .collect[JsObject]

      result must not be empty.await
      result must beAnInstanceOf[List[JsObject]].await
      result must beEqualTo(collectionContent).await
    }
  }

  override def after = {
    collection.drop
  }
}
