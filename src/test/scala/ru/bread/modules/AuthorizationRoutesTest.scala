package ru.bread.modules

import akka.http.scaladsl.model.StatusCodes
import ru.bread.http.response.Completed
import ru.bread.services.SessionId
import utils.TestStuff
import test.data.AuthTestData._
import ru.bread.http.response.JsonParsers._
import ru.bread.routes.AuthorizationRoutes
import ru.bread.services.external.AuthorizationService
import ru.bread.services.internal.ValidationService

class AuthorizationRoutesTest extends TestStuff {

  trait mocks {

    val authorizationService = stub[AuthorizationService]
    val validationService = stub[ValidationService]

    val module = new AuthorizationRoutes(authorizationService, validationService)

    (authorizationService.authorize _).when(trueCredentials).returns(Some(wrappedSessionId))
    (validationService.validateUser _).when(registrationUser).returns(Completed)
    (authorizationService.registerNewUser _).when(registrationUser).returns(Completed)

    val routes = module.routes()
  }

  "register_user" should "register user successful" in new mocks {
    Post("/register_user", registrationUser) ~> routes ~> check {

      responseAs[String] shouldEqual "\"Done\""
      status shouldBe StatusCodes.OK
    }
  }

  "sign_up" should "return sessionId" in new mocks {

    Post("/sign_up") ~> addCredentials(basicHttpCredentials) ~> routes ~> check {
      responseAs[SessionId] shouldEqual wrappedSessionId
      status shouldBe StatusCodes.OK
    }
  }


}

