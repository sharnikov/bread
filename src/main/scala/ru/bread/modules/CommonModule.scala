package ru.bread.modules

import ru.bread.services.caching.{BreadCacheService, CacheService}
import ru.bread.services.internal._
import ru.bread.services.security._
import ru.bread.settings.config.Settings

class CommonModule(settings: Settings) extends Module {
  override def name(): String = "Common"

  val timeProvider: TimeProvider = new SystemTimeProvider(settings)
  val encryptService: EncryptService = new MD5EncryptService(settings)
  val sessionGenerator: SessionGenerator = new SessionGeneratorImpl(settings)
  val SSLContextProducer: SSLContextProducer = new SimpleSSLContextProducer(settings)
  val cacheService: CacheService = new BreadCacheService(settings)

}
