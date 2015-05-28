package com.evojam

import play.api.Configuration

import com.evojam.mongodb.client.MongoClients
import com.evojam.mongodb.play.json.Codec

trait MongoProvider {
  private def clientForName(name: ConnectionName, configuration: MongoModuleConfiguration) =
    configuration.connections.get(name)
      .map(MongoClients.buildSettings(_).codecRegistry(Codec.registry))
      .map(MongoClients.create(_))

  def mongoClient(connectionName: String)(implicit configuration: Configuration) =
    clientForName(new ConnectionName(connectionName), MongoModuleConfiguration(configuration))
}
