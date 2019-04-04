package ru.bread.settings.config

import com.typesafe.config.Config

trait Common {
  def salt(): String
  def timeZone(): Int
}

class CommonSettings(config: Config) extends Common {
  override def salt(): String = config.getString("salt")
  override def timeZone(): Int = config.getInt("timezone")
}
