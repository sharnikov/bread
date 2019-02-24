package settings.config

import com.typesafe.config.Config
import scala.concurrent.duration.Duration
import Settings._

trait Session {
  def lifetimePeriod(): Duration
}

class SessionSettings(config: Config) extends Session {
  override def lifetimePeriod(): Duration = config.getDuration("lifetime").toScalaDuration()
}
