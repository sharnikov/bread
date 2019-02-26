package ru.bread.settings.config

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

import scala.concurrent.duration.Duration

trait Settings {
  def akkaSettings(): Akka
  def sessionSettings(): Session
  def schedulerSettings(): Scheduler
}

class AppSettings(config: Config) extends Settings {
  override def akkaSettings: Akka = new AkkaSettings(config.as[Config]("akka.ru.bread.http"))
  override def sessionSettings(): Session = new SessionSettings(config.as[Config]("session"))
  override def schedulerSettings(): Scheduler = new SchedulerSettings(config.as[Config]("scheduler"))
}

object Settings {
  implicit class duration(duration: java.time.Duration) {
    def toScalaDuration() = Duration.fromNanos(duration.toNanos)
  }

}
