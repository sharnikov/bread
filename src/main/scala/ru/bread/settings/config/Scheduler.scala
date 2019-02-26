package ru.bread.settings.config

import com.typesafe.config.Config

import scala.concurrent.duration.Duration
import Settings._

trait Scheduler {
  def treadsAmount(): Int
  def repeatRate(): Duration
}

class SchedulerSettings(config: Config) extends Scheduler {
  override def treadsAmount(): Int = config.getInt("threads.amount")
  override def repeatRate(): Duration = config.getDuration("repeat.rate").toScalaDuration()
}
