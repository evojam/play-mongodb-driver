Play Framework MongoDB module
===========================

Fully asynchronous, lightweight and convenient in usage for Scala developers integrated with **Play Framework 2.4**. Write more later...

## How to enable the module

Add library dependency in your build:

    libraryDependencies += "com.evojam" % "play-mongodb-driver_2.11" % "0.1.0-SNAPSHOT"

Enable module in the application configuration and provide [mongo connection string](http://docs.mongodb.org/manual/reference/connection-string/):

    play.modules.enabled += "com.evojam.MongoModule"
    mongo.db.default.uri = "mongodb://localhost/test"

Voila, now you can use it anywhere... :) We strongly discourage mixing dao in the controller but for an example the code sample below is good enough:
    
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

## How to query
TODO
