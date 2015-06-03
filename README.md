Play Framework MongoDB module
===========================

![Travis Build Status](https://travis-ci.org/evojam/play-mongodb-driver.svg)

The major goal was to provide the convenient to use library for the MongoDB for those who work with **Play Framework 2.4.x** and it's [`ScalaJson library`](https://www.playframework.com/documentation/2.4.x/ScalaJson). This MongoDB client has beed developed on the top of the [mongodb-driver-scala](https://github.com/evojam/mongodb-driver-scala).

We provide `Codec` for all classes extending the `JsValue`. For sake of convenience we expose the collection's implementation that allows you to fetch query results as the [extended JSON](http://docs.mongodb.org/manual/reference/mongodb-extended-json/) or custom objects that have defined the `Reader` and pass query filters and parameters as either a `JsValue` objects or objects that have an `Writer` available.

## How to enable the module

Currently, we are publishing to the Sonatype snapshots thus this is the repository that has to be enabled. In your `build.sbt` add if not yet enabled:

    resolvers += Resolver.sonatypeRepo("snapshots")

Add library dependency:

    libraryDependencies += "com.evojam" % "play-mongodb-driver_2.11" % "0.1.0-SNAPSHOT"

Enable module in the application configuration and provide [mongo connection string](http://docs.mongodb.org/manual/reference/connection-string/):

    play.modules.enabled += "com.evojam.MongoModule"
    mongo.db.default.uri = "mongodb://localhost/test"

Voila, now you can use it anywhere... :) We strongly discourage mixing dao in the controller but for an example the controller with the endpoint returning list of databases is good enough:

    package com.evojam.demo.controller

    import play.api.libs.json.Json
    import play.api.mvc.{ Action, Controller }

    import com.evojam.mongodb.client.MongoClient
    import com.google.inject.Inject

    import scala.concurrent.ExecutionContext.Implicits.global

    class DemoController @Inject()(mongo: MongoClient) extends Controller {

      def get() = Action.async(parse.empty) { _ =>
        mongo.listDatabaseNames().map(Json.toJson(_)).map(Ok(_))
      }
    }

## How to query for real

    val collection: JsonCollection = ???

    collection.find().collect() ???

    TODO: Fill this sample when the code is ready
