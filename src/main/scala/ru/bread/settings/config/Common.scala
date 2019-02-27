package ru.bread.settings.config

import com.typesafe.config.Config

trait Common {
  def salt(): String
}

class CommonSettings(config: Config) extends Common {
  override def salt(): String = config.getString("salt")
}
