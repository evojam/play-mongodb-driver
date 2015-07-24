package com.evojam.mongodb.play.json

import com.mongodb.ConnectionString

import com.evojam.mongodb.client.{MongoClients => CoreMongoClients, MongoClientSettings, MongoClient}

object MongoClients {
  def create(): MongoClient =
    create(new ConnectionString("mongodb://localhost"))

  def create(connectionString: String): MongoClient =
    create(new ConnectionString(connectionString))

  def create(connectionString: ConnectionString): MongoClient =
    create(CoreMongoClients.buildSettings(connectionString))

  def create(settings: MongoClientSettings): MongoClient =
    CoreMongoClients.create(settings.codecRegistry(codecRegistry))
}
