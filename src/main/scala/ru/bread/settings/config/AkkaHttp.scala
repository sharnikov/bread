package ru.bread.settings.config

import com.typesafe.config.Config

trait AkkaHttp {
  def host: String
  def port: Int
}

class AkkaHttpSettings(config: Config) extends AkkaHttp {
  override def host: String = config.getString("host")
  override def port: Int = config.getInt("port")
}
