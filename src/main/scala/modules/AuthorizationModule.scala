package modules

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import akka.http.scaladsl.server.Directives._
import database.UserDAOImpl
import http.RoutesUtils
import services.{AuthorizationService, LogAndPass, SimpleAuthorizationService}
import settings.JsonParsers._
import akka.http.scaladsl.server.Route
import settings.config.Settings

class AuthorizationModule(dbModule: DatabaseModule, settings: Settings) extends ModuleWithRoutes with RoutesUtils {
  override def name(): String = "Authorization"

  val sessions: ConcurrentHashMap[String, Date] = new ConcurrentHashMap[String, Date]()

  val userDao = new UserDAOImpl(dbModule.dbSchema)
  val authorizationService = new SimpleAuthorizationService(userDao, sessions, settings)

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
