package modules

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import akka.http.scaladsl.server.Directives._
import database.UserDAOImpl
import http.Routes
import services.{AuthorizationService, LogAndPass, SimpleAuthorizationService}
import settings.JsonParsers._
import akka.http.scaladsl.server.Route
import settings.config.Settings
import settings.schedulers.MainContext

class AuthorizationModule(dbModule: DatabaseModule, settings: Settings) extends Module with Routes with MainContext {
  override def name(): String = "Authorization"

  val sessions: ConcurrentHashMap[String, Date] = new ConcurrentHashMap[String, Date]()

  val userDao = new UserDAOImpl(dbModule.dbSchema)
  val authorizationService = new SimpleAuthorizationService(userDao, sessions, settings)

  override def routes() = routes(authorizationService)

  def routes(authorizationService: AuthorizationService) =
    Route.seal(
      post {
        path("login") {
          entity(as[LogAndPass]) { logAndPass =>
            completeResult(authorizationService.login(logAndPass.login, logAndPass.password))
          }
        }
      }
    )
}
