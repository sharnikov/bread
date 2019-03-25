package ru.bread.settings.config

import com.typesafe.config.Config

trait ConfigExtractor {
  def getConfig(): Config
}
