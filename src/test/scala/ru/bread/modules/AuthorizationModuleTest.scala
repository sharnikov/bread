package ru.bread.modules

import akka.http.scaladsl.model.StatusCodes
import ru.bread.http.Response.SuccessfulResponse
import ru.bread.services.{AuthorizationService, SessionId}
import ru.bread.settings.config.Settings
import utils.TestStuff
import test.data.AuthTestData._
import ru.bread.settings.JsonParsers._
import ru.bread.http.Response._

class AuthorizationModuleTest extends TestStuff {

  trait mocks {
    val dbModule = stub[DatabaseModule]
    val settings = stub[Settings]
    val authorizationService = stub[AuthorizationService]
    val module = new AuthorizationModule(dbModule, settings)
    val routes = module.routes(authorizationService)

    (authorizationService.login _).when(login, password).returns(sessionId)
  }

  "login" should "return sessionId" in new mocks {

    Post("/login", logAndPass) ~> routes ~> check {
      responseAs[SuccessfulResponse[SessionId]].payload shouldEqual sessionId
      status shouldBe StatusCodes.OK
    }
  }
}

