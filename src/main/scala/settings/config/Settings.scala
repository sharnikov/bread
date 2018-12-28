package settings.config

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

trait Settings {
  def akkaSettings(): Akka
  def databaseSettings(): Database
}

class AppSettings(config: Config) extends Settings {
  override def akkaSettings: Akka = new AkkaSettings(config.as[Config]("akka.http"))
  override def databaseSettings(): Database = new DatabaseSettings(config.as[Config]("bread.db"))
}
