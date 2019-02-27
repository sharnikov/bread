package ru.bread.modules

import ru.bread.services.internal.{MD5EncryptService, SessionGeneratorImpl, SystemTimeProvider, TimeProvider}
import ru.bread.settings.config.Settings

class CommonModule(settings: Settings) extends Module {
  override def name(): String = "Common"

  val timeProvider: TimeProvider = new SystemTimeProvider()
  val encryptService = new MD5EncryptService(settings)
  val sessionGenerator = new SessionGeneratorImpl(settings)

}
