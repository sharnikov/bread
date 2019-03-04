package ru.bread.modules

import ru.bread.services.internal._
import ru.bread.settings.config.Settings

class CommonModule(settings: Settings) extends Module {
  override def name(): String = "Common"

  val timeProvider: TimeProvider = new SystemTimeProvider()
  val encryptService: EncryptService = new MD5EncryptService(settings)
  val sessionGenerator: SessionGenerator = new SessionGeneratorImpl(settings)

}
