package com.evojam

import play.api.Configuration

import com.evojam.mongodb.client.MongoClients

trait MongoProvider {
  private def clientForName(name: ConnectionName, configuration: MongoModuleConfiguration) =
    configuration.connections.get(name).map(MongoClients.create(_))

  def mongoClient(connectionName: String)(implicit configuration: Configuration) =
    clientForName(new ConnectionName(connectionName), MongoModuleConfiguration(configuration))
}
