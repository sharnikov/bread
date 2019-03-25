package ru.bread.settings.config

import com.typesafe.config.Config
import Settings._

import scala.concurrent.duration.Duration

trait Cache extends ConfigExtractor {
  def maxCapacity(): Int
  def initCapacity(): Int
  def lifetimePeriod(): Duration
  def idletimePeriod(): Duration
}

class CacheSettings(config: Config) extends Cache {
  override def maxCapacity(): Int = config.getInt("max.capacity")
  override def initCapacity(): Int = config.getInt("initial.capacity")
  override def lifetimePeriod(): Duration = config.getDuration("lifetime.period").toScalaDuration()
  override def idletimePeriod(): Duration = config.getDuration("idle.period").toScalaDuration()
  override def getConfig(): Config = config
}
