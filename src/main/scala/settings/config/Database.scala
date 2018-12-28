package settings.config

import com.typesafe.config.Config

trait Database {
  def host(): String
  def port(): Int
  def user(): String
  def password(): String
  def database(): String
  def databaseConfigName(): String
}

class DatabaseSettings(config: Config) extends Database {
  override def host(): String = config.getString("host")
  override def port(): Int = config.getInt("port")
  override def user(): String = config.getString("user")
  override def password(): String = config.getString("password")
  override def database(): String = config.getString("database")
  override def databaseConfigName(): String = "db.bread"
}
