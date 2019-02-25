package modules

import akka.http.scaladsl.model.StatusCodes
import http.Response.SuccessfulResponse
import services.{AuthorizationService, SessionId}
import settings.config.Settings
import utils.TestStuff
import test.data.AuthTestData._
import settings.JsonParsers._
import http.Response._

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

