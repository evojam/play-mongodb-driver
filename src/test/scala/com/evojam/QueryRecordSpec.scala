package com.evojam

import play.api.libs.json.Json

import org.specs2.mutable.After
import org.specs2.mutable.Specification

import com.evojam.mongodb.client.MongoClients
import com.evojam.mongodb.client.MongoClientSettings
import com.evojam.mongodb.play.json.Codec
import com.evojam.mongodb.play.json.Codec._

class QueryRecordSpec extends Specification with After {

  case class SampleRecord(_id: String)

  object SampleRecord {
    implicit val format = Json.format[SampleRecord]
  }

  val collection = MongoClients.create(
    MongoClientSettings().codecRegistry(Codec.registry))
    .getDatabase("foodb")
    .collection("queryrecordspec")

  val collectionContent = List(
    SampleRecord("first"),
    SampleRecord("second"),
    SampleRecord("third"))

  sequential

  "MongoClient" should {
    "insert list of SampleRecord objects to a collection" in {
      val count = collection
        .insertAll(collectionContent)
        .flatMap(_ => collection.count)

      count must beEqualTo(collectionContent.size).await
    }

    "query SampleRecords from a collection" in {
      val records = collection
        .find()
        .collect[SampleRecord]

      records must beEqualTo(collectionContent).await
    }
  }

  override def after = {
    collection.drop
  }
}
