package ru.bread.settings.config

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

import scala.concurrent.duration.Duration

trait Settings {
  def akkaSettings(): Akka
  def sessionSettings(): Session
  def schedulerSettings(): Scheduler
  def commonSettings(): Common
  def sslSettings(): SSL
}

class AppSettings(config: Config) extends Settings {
  override def akkaSettings: Akka = new AkkaSettings(config.as[Config]("akka.http"))
  override def sessionSettings(): Session = new SessionSettings(config.as[Config]("session"))
  override def schedulerSettings(): Scheduler = new SchedulerSettings(config.as[Config]("scheduler"))
  override def commonSettings(): Common = new CommonSettings(config.as[Config]("common"))
  override def sslSettings(): SSL = new SSLSettings(config.as[Config]("ssl"))
}

object Settings {
  implicit class duration(duration: java.time.Duration) {
    def toScalaDuration() = Duration.fromNanos(duration.toNanos)
  }
}
