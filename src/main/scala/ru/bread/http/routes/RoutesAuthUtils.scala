package ru.bread.http.routes

import akka.http.scaladsl.server.Directives.headerValueByName
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging
import ru.bread.database.User
import ru.bread.services.external.AuthorizationService

trait RoutesAuthUtils extends LazyLogging {

  val authorizationService: AuthorizationService

  def authWithToken(routesToAuth: User => Route): Route = headerValueByName("sessionid") { token =>
    val user = authorizationService.userBySessionId(token)
    logger.info(s"User ${user.id} was successfully authenticated")
    routesToAuth(user)
  }

}
