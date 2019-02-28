package ru.bread.modules

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging
import ru.bread.database.services.UserDAOImpl
import ru.bread.http.routes.RoutesUtils
import ru.bread.http.response.Response._
import ru.bread.http.response.JsonParsers._
import ru.bread.services.external.{AuthorizationService, BasicAuthorizationService}
import ru.bread.settings.config.Settings

class AuthorizationModule(dbModule: DatabaseModule,
                          commonModule: CommonModule,
                          settings: Settings) extends ModuleWithRoutes with RoutesUtils with LazyLogging {
  override def name(): String = "Authorization"

  val sessions: ConcurrentHashMap[String, Date] = new ConcurrentHashMap[String, Date]()

  val userDao = new UserDAOImpl(dbModule.dbSchema)
  val authorizationService = new BasicAuthorizationService(
    userDAO = userDao,
    sessionGenerator = commonModule.sessionGenerator,
    encryptService = commonModule.encryptService,
    timeProvider = commonModule.timeProvider,
    sessions = sessions
  )

  override def routes(): Route = routes(authorizationService)

  def routes(authorizationService: AuthorizationService) =
    authenticateBasicAsync("ordersAuth", authorizationService.authorize) { sessionId =>
        post {
          path("sign_up") {
            complete(success(sessionId))
          }
      }
    }
}
