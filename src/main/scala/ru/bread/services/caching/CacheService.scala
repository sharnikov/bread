package ru.bread.services.caching

import akka.http.caching.LfuCache
import akka.http.caching.scaladsl.{Cache, CachingSettings}
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.directives.CachingDirectives._
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import ru.bread.database.User
import ru.bread.settings.config.Settings

trait CacheService {
  def cacheRouteByUrl(route: Route): Route
  def cacheRouteByUser(user: User)(route: Route): Route
  def invalidateByUser(user: User)(route: Route): Route
}

class BreadCacheService(settings: Settings) extends CacheService with CacheSettings {

  private val uriKeyerFunction: PartialFunction[RequestContext, Uri] = {
    case r: RequestContext ⇒ r.request.uri
  }

  private val uriKeyCache: Cache[Uri, RouteResult] = LfuCache(getCacheSettings(settings))
  private val uriCacheFunction = cache(uriKeyCache, uriKeyerFunction)(_)
  private val userKeyCache: Cache[User, RouteResult] = LfuCache(getCacheSettings(settings))

  override def cacheRouteByUrl(route: Route): Route = uriCacheFunction(route)

  override def cacheRouteByUser(user: User)(route: Route): Route = {
    val userKeyerFunction: PartialFunction[RequestContext, User] = {
      case _ => user
    }
    cache(userKeyCache, userKeyerFunction)(route)
  }

  override def invalidateByUser(user: User)(route: Route): Route = {
    userKeyCache.remove(user)
    route
  }
}