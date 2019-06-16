package ru.bread.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging
import ru.bread.database.RegistrationUser
import ru.bread.http.response.JsonParsers._
import ru.bread.http.routes.RoutesUtils
import ru.bread.modules.ModuleWithRoutes
import ru.bread.services.external.AuthorizationService
import ru.bread.services.internal.ValidationService
import ru.bread.settings.schedulers.MainContext

class AuthorizationRoutes(authorizationService: AuthorizationService,
                          validationService: ValidationService) extends ModuleWithRoutes with RoutesUtils with LazyLogging
  with MainContext {

  override def name(): String = "Authorization routes module"

  override def routes(): Route =
    post {
      path("register_user") {
        entity(as[RegistrationUser]) { user =>
          completeResult(
            for {
              _ <- validationService.validateUser(user)
              result <- authorizationService.registerNewUser(user)
            } yield result
          )
        }
      } ~ path("sign_up") {
        authenticateBasicAsync("ordersAuth", authorizationService.authorize) { sessionId =>
          complete(sessionId)
        }
      }
    }
}
