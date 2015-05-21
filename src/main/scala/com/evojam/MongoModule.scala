package com.evojam

import scala.util.{ Failure, Try }

import play.api.inject.{ Binding, Module }
import play.api._
import scala.collection.JavaConversions._

import com.evojam.mongodb.client.{ MongoClients, MongoClient }
import com.typesafe.config.ConfigValue

import com.typesafe.config.ConfigObject

class MongoModule extends Module {
  val logger = Logger(getClass)

  def maybeClient(config: ConfigValue): Try[MongoClient] =
    config match {
      case o: ConfigObject =>
        Option(o.get("uri")).map(_.unwrapped()).map(_.toString).map { uri =>
          logger.info(s"Connecting to MongoDB uri=$uri")
          Try(MongoClients.create(uri))
        }.getOrElse(Failure(new Exception("Missing database uri in configuration object")))
      case _ =>
        Failure(new Exception("Refuse to configure MongoClient, expecting configuration object"))
    }

  def bindDefault(name: String, config: ConfigValue): Seq[Binding[_]] =
    maybeClient(config).map(client =>
      Seq(bind[MongoClient].toInstance(client), bind[MongoClient].qualifiedWith(name).toInstance(client)))
      .recoverWith {
      case e =>
        logger.error(s"Failed to create MongoClient for configuration named=$name", e)
        Failure(e)
    }.toOption.getOrElse(Seq.empty[Binding[MongoClient]])


  def bindWithName(name: String, config: ConfigValue): Option[Binding[_]] =
    maybeClient(config).map(client =>
      Some(bind[MongoClient].qualifiedWith(name).toInstance(client)))
      .recoverWith {
      case e =>
        logger.error(s"Failed to create MongoClient for configuration named=$name", e)
        Failure(e)
    }.toOption.flatten

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    configuration.getObject("mongo.db").flatMap(dbs =>
      dbs.toSeq.partition(_._1 == "default") match {
        case (defaults, others) =>
          defaults.headOption.map((_, others))
            .orElse(others.headOption.map((_, others.drop(1)))).map {
            case (default, other) =>
              bindDefault(default._1, default._2) ++ other.flatMap(config => bindWithName(config._1, config._2))
          }
      }).getOrElse {
      logger.warn("MongoModule enabled but database not configured. Provide mongo.db.default in " +
                  "configuration.")
      Seq.empty
    }
}
