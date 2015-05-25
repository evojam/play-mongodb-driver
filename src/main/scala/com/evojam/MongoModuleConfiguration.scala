package com.evojam

import scala.util.{ Failure, Try }

import play.api.{ Configuration, Logger }

import com.mongodb.ConnectionString

case class ConnectionName(wrapped: String)

case class MongoModuleConfiguration(default: Option[ConnectionName],
  connections: Map[ConnectionName, ConnectionString]) {
  require(default != null, "default cannot be null")
  require(connections != null, "connections cannot be null")
}

object MongoConnectionConfigurationMalformed extends Exception("Connection uri is required")

case class MongoConnectionConfigurationError(reason: Throwable) extends Exception("Configuration error", reason)

object MongoModuleConfiguration {
  val logger = Logger(getClass)

  private def build(in: Map[ConnectionName, ConnectionString]): MongoModuleConfiguration =
    new MongoModuleConfiguration(in.keys.find(_.wrapped == "default").orElse(in.keys.headOption), in)

  private def build(in: Configuration): MongoModuleConfiguration =
    build(in.subKeys.flatMap(key => in.getConfig(key).map(key -> _)).flatMap {
      case (connectionName, connectionConfig) =>
        connectionConfig.getString("uri")
          .map(uri => Try(new ConnectionString(uri)).recoverWith {
          case iae: IllegalArgumentException =>
            Failure(MongoConnectionConfigurationError(iae))
        }).getOrElse(Failure[ConnectionString](MongoConnectionConfigurationMalformed))
          .map(connectionString => Some(new ConnectionName(connectionName) -> connectionString)).recover {
          case e =>
            logger.error(s"Cannot configure MongoDB connection for configuration=$connectionName, skipping.", e)
            None
        }.toOption.flatten.toSeq
    }.toMap)

  def apply(in: Configuration): MongoModuleConfiguration =
    in.getConfig("mongo.db").map(build)
      .getOrElse(new MongoModuleConfiguration(None, Map.empty))

  def apply(default: ConnectionName, connections: Map[ConnectionName, ConnectionString]): MongoModuleConfiguration =
    new MongoModuleConfiguration(Some(default), connections)


  def apply() = new MongoModuleConfiguration(None, Map.empty)
}