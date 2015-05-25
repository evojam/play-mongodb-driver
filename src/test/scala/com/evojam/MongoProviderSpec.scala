package com.evojam

import play.api.Configuration

import com.mongodb.ConnectionString
import org.specs2.mutable.Specification

class MongoProviderSpec extends Specification with MongoProvider {

  val defaultUri = "mongodb://localhost/default"
  val otherUri1 = "mongodb://localhost/other1"
  val otherUri2 = "mongodb://localhost/other2"

  val defaultConnectionName = new ConnectionName("default")
  val otherConnectionName1 = new ConnectionName("other1")
  val otherConnectionName2 = new ConnectionName("other2")

  "configuration with explicit default database" should {
    val configurationWithDefault =
      Configuration.from(Map("mongo" ->
                             Map("db" -> Map(
                               "default" -> Map("uri" -> defaultUri),
                               "other1" -> Map("uri" -> otherUri1),
                               "other2" -> Map("uri" -> otherUri2)))))

    val mmc = MongoModuleConfiguration(
      defaultConnectionName,
      Map(
        defaultConnectionName -> new ConnectionString(defaultUri),
        otherConnectionName1 -> new ConnectionString(otherUri1),
        otherConnectionName2 -> new ConnectionString(otherUri2)))

    "build valid configuration" in {
      MongoModuleConfiguration(configurationWithDefault) must be equalTo mmc
    }
  }

  "configuration without explicit default database" should {

    val configurationWithoutDefault =
      Configuration.from(Map("mongo" ->
                             Map("db" -> Map(
                               "other1" -> Map("uri" -> otherUri1),
                               "other2" -> Map("uri" -> otherUri2)))))

    val mmc = MongoModuleConfiguration(
      otherConnectionName1,
      Map(
        otherConnectionName1 -> new ConnectionString(otherUri1),
        otherConnectionName2 -> new ConnectionString(otherUri2)))

    "build valid configuration" in {
      MongoModuleConfiguration(configurationWithoutDefault) must be equalTo mmc
    }
  }

  "empty configuration" should {
    val configurationEmpty = Configuration.empty

    "build valid configuration" in {
      MongoModuleConfiguration(configurationEmpty) must be equalTo MongoModuleConfiguration()
    }
  }

  "malformed configuration" should {
    val configurationMalformed =
      Configuration.from(Map("mongo" ->
                             Map("db" -> Map(
                               "other1" -> Map("uris" -> otherUri1)))))

    "build valid configuration" in {
      MongoModuleConfiguration(configurationMalformed) must be equalTo MongoModuleConfiguration()
    }
  }

  "configuration with malformed uri" should {
    val configurationMalformedUri =
      Configuration.from(Map("mongo" ->
                             Map("db" -> Map(
                               "other1" -> Map("uri" -> "mon://go")))))

    "build valid configuration" in {
      MongoModuleConfiguration(configurationMalformedUri) must be equalTo MongoModuleConfiguration()
    }
  }
}
