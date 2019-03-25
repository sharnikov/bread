package ru.bread.services.caching

import akka.http.caching.scaladsl.CachingSettings
import com.typesafe.config.ConfigFactory
import ru.bread.settings.config.Settings

trait CacheSettings {

  def getCacheSettings(settings: Settings): CachingSettings = {

    val cacheSetting = settings.cacheSettings()
    val defaultCachingSettings = CachingSettings(ConfigFactory.load())

    val lfuCacheSettings =
      defaultCachingSettings.lfuCacheSettings
        .withInitialCapacity(cacheSetting.initCapacity())
        .withMaxCapacity(cacheSetting.maxCapacity())
        .withTimeToLive(cacheSetting.lifetimePeriod())
        .withTimeToIdle(cacheSetting.idletimePeriod())

      defaultCachingSettings.withLfuCacheSettings(lfuCacheSettings)
  }
}
