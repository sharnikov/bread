package ru.bread.modules

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import akka.http.scaladsl.server.Directives._
import ru.bread.services.LogAndPass
import ru.bread.http.response.JsonParsers._
import akka.http.scaladsl.server.Route
import ru.bread.database.services.UserDAOImpl
import ru.bread.http.routes.RoutesUtils
import ru.bread.services.external.{AuthorizationService, SimpleAuthorizationService}
import ru.bread.settings.config.Settings

class AuthorizationModule(dbModule: DatabaseModule,
                          commonModule: CommonModule,
                          settings: Settings) extends ModuleWithRoutes with RoutesUtils {
  override def name(): String = "Authorization"

  val sessions: ConcurrentHashMap[String, Date] = new ConcurrentHashMap[String, Date]()

  val userDao = new UserDAOImpl(dbModule.dbSchema)
  val authorizationService = new SimpleAuthorizationService(userDao, commonModule.timeProvider, sessions, settings)

  override def routes(): Route = routes(authorizationService)

  def routes(authorizationService: AuthorizationService) =
      post {
        path("login") {
          entity(as[LogAndPass]) { logAndPass =>
            completeResult(authorizationService.login(logAndPass.login, logAndPass.password))
          }
        }
      }
}
