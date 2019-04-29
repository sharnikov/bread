package ru.bread.modules

import ru.bread.database.services.UserDAO
import ru.bread.services.caching.{BreadCacheService, CacheService}
import ru.bread.services.internal._
import ru.bread.services.security._
import ru.bread.settings.config.Settings

class CommonModule(userDao: UserDAO, settings: Settings) extends Module {
  override def name(): String = "Common"

  val timeProvider: TimeProvider = new SystemTimeProvider(settings)
  val encryptService: EncryptService = new MD5EncryptService(settings)
  val sessionGenerator: SessionGenerator = new SessionGeneratorImpl(settings)
  val sslContextProducer: SSLContextProducer = new SimpleSSLContextProducer(settings)
  val cacheService: CacheService = new BreadCacheService(settings)
  val validationService: ValidationService = new ValidationServiceImpl(userDao)

}
