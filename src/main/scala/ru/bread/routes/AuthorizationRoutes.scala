package ru.bread.routes

import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.LazyLogging
import ru.bread.database.RegistrationUser
import ru.bread.http.response.JsonParsers._
import ru.bread.http.routes.RoutesUtils
import ru.bread.modules.ModuleWithRoutes
import ru.bread.services.external.AuthorizationService
import ru.bread.services.internal.ValidationService
import ru.bread.settings.config.Settings
import ru.bread.settings.schedulers.MainContext

class AuthorizationRoutes(authorizationService: AuthorizationService,
                          validationService: ValidationService,
                          settings: Settings) extends ModuleWithRoutes with RoutesUtils with LazyLogging
  with MainContext {

  override def name(): String = "Authorization routes module"

  override def routes() =
    authenticateBasicAsync("ordersAuth", authorizationService.authorize) { sessionId =>
        post {
          path("sign_up") {
            complete(sessionId)
          }
      }
    } ~ post {
      path("register_user") {
        entity(as[RegistrationUser]) { user =>
          complete(
            for {
              _ <- validationService.validateUser(user)
              result <- authorizationService.registerNewUser(user)
            } yield result
          )
        }
      }
    }
}
