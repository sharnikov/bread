package ru.bread.modules

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging
import ru.bread.database.User
import ru.bread.database.services.{UserDAO, UserDAOImpl}
import ru.bread.http.routes.RoutesUtils
import ru.bread.http.response.Response._
import ru.bread.http.response.JsonParsers._
import ru.bread.modules.AuthorizationModule.{Session, SessionStorage}
import ru.bread.services.external.{AuthorizationService, BasicAuthorizationService}
import ru.bread.settings.config.Settings

class AuthorizationModule(dbModule: DatabaseModule,
                          commonModule: CommonModule,
                          settings: Settings) extends ModuleWithRoutes with RoutesUtils with LazyLogging {
  override def name(): String = "Authorization"

  val sessions: SessionStorage = new ConcurrentHashMap[String, Session]()

  val userDao: UserDAO = new UserDAOImpl(dbModule.dbSchema)
  val authorizationService: AuthorizationService = new BasicAuthorizationService(
    userDAO = userDao,
    sessionGenerator = commonModule.sessionGenerator,
    encryptService = commonModule.encryptService,
    timeProvider = commonModule.timeProvider,
    sessions = sessions
  )

  def getUserDao()= userDao
  def getAuthorizationService() = authorizationService

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

object AuthorizationModule {
  type SessionStorage = ConcurrentHashMap[String, Session]

  case class Session(user: User, expireDate: Date)
}
