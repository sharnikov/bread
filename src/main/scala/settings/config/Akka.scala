package settings.config

import com.typesafe.config.Config

trait Akka {
  def host: String
  def port: Int
}

class AkkaSettings(config: Config) extends Akka {
  override def host: String = config.getString("host")
  override def port: Int = config.getInt("port")
}
