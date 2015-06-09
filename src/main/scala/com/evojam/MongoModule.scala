package com.evojam

import play.api._
import play.api.inject.{ Binding, Module }

import com.evojam.mongodb.client.{ MongoDatabase, MongoCollection, MongoClient, MongoClients }
import com.google.inject.Inject

class MongoModule @Inject()(environment: Environment, config: Configuration) extends Module {
  val logger = Logger(getClass)

  val configuration = MongoModuleConfiguration(config)

  val connectedClients = configuration.connections.map {
    case (name, connectionString) => name -> MongoClients.create(connectionString)
  }

  def bindDefault(name: ConnectionName, client: MongoClient): Seq[Binding[_]] =
    Seq(
      bind[MongoClient].toInstance(client),
      bind[MongoClient].qualifiedWith(name.wrapped).toInstance(client),
      bind[MongoDatabase].toInstance(client.database()),
      bind[MongoDatabase].qualifiedWith(name.wrapped).toInstance(client.database()))

  def bindWithName(name: ConnectionName, client: MongoClient): Seq[Binding[_]] = Seq(
    bind[MongoClient].qualifiedWith(name.wrapped).toInstance(client),
    bind[MongoDatabase].qualifiedWith(name.wrapped).toInstance(client.database()))

  override def bindings(e: Environment, c: Configuration): Seq[Binding[_]] =
    configuration.default.map { defaultConnectionName =>
      connectedClients.get(defaultConnectionName)
        .map(bindDefault(defaultConnectionName, _))
        .getOrElse(Seq.empty) ++
      configuration.connections.keys
        .filterNot(_ == defaultConnectionName).flatMap(connectionName =>
        connectedClients.get(connectionName).toSeq.flatMap(bindWithName(connectionName, _)))
    }.getOrElse(connectedClients.flatMap {
      case (name, connectionString) => bindWithName(name, connectionString)
    }).toSeq
}