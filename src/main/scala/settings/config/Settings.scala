package settings.config

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

trait Settings {
  def akkaSettings(): Akka
}

class AppSettings(config: Config) extends Settings {
  override val akkaSettings: Akka = new AkkaSettings(config.as[Config]("akka.http"))
}
