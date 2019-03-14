package ru.bread.settings.config

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

trait SSL {
  def keyStorage: KeyStorage
  def keyManagerAlgorithm: String
  def trustManagerAlgorithm: String
  def sslAlgorithm: String
}

class SSLSettings(config: Config) extends SSL {
  override def keyStorage: KeyStorage = new KeyStorageSettings(config.as[Config]("key.storage"))
  override def keyManagerAlgorithm: String = config.getString("key.manager.algorithm")
  override def trustManagerAlgorithm: String = config.getString("trust.manager.algorithm")
  override def sslAlgorithm: String = config.getString("ssl.algorithm")
}


trait KeyStorage {
  def password: String
  def path: String
  def storageType: String
}

class KeyStorageSettings(config: Config) extends KeyStorage {
  override def password: String = config.getString("password")
  override def path: String = config.getString("path")
  override def storageType: String = config.getString("type")
}