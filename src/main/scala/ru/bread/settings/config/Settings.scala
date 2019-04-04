package ru.bread.settings.config

import java.util.concurrent.atomic.AtomicReference

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

import scala.concurrent.duration.Duration

trait Settings {
  def akkaHttpSettings(): AkkaHttp
  def sessionSettings(): Session
  def schedulerSettings(): Scheduler
  def commonSettings(): Common
  def sslSettings(): SSL
  def cacheSettings(): Cache
}

class AppSettings(config: Config) extends Settings {
  override def akkaHttpSettings: AkkaHttp = new AkkaHttpSettings(config.as[Config]("akka.http"))
  override def sessionSettings(): Session = new SessionSettings(config.as[Config]("session"))
  override def schedulerSettings(): Scheduler = new SchedulerSettings(config.as[Config]("scheduler"))
  override def commonSettings(): Common = new CommonSettings(config.as[Config]("common"))
  override def sslSettings(): SSL = new SSLSettings(config.as[Config]("ssl"))
  override def cacheSettings(): Cache = new CacheSettings(config.as[Config]("caching"))
}

object Settings {
  implicit class duration(duration: java.time.Duration) {
    def toScalaDuration() = Duration.fromNanos(duration.toNanos)
  }
}

class UpdatableAppSettings(source: () => Settings) extends Settings {

  private lazy val settingsState = new AtomicReference[Settings](source())

  def update(): Unit = synchronized {
    settingsState.set(source())
  }

  override def akkaHttpSettings(): AkkaHttp = settingsState.get().akkaHttpSettings()
  override def sessionSettings(): Session = settingsState.get().sessionSettings()
  override def schedulerSettings(): Scheduler = settingsState.get().schedulerSettings()
  override def commonSettings(): Common = settingsState.get().commonSettings()
  override def sslSettings(): SSL = settingsState.get().sslSettings()
  override def cacheSettings(): Cache = settingsState.get().cacheSettings()
}
