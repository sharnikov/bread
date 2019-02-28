package ru.bread.services.external

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import ru.bread.database.services.UserDAO
import ru.bread.errors.AppError.VerboseServiceException
import ru.bread.services.internal.{EncryptService, FixedTimeProvider, SessionGenerator}
import ru.bread.settings.config.TestSettings
import utils.TestStuff
import test.data.CommonTestData._
import test.data.AuthTestData._

class AuthorizationServiceTest extends TestStuff with TestSettings {

  trait mocks {
    val userDAO = stub[UserDAO]
    val sessionGenerator = stub[SessionGenerator]
    val encryptService = stub[EncryptService]
    val timeProvider = new FixedTimeProvider(time)
    val sessions = stub[ConcurrentHashMap[String, Date]]

    (session.size _).when().returns(30)

    val authorizationService = new BasicAuthorizationService(
      userDAO,
      sessionGenerator,
      encryptService,
      timeProvider,
      sessions
    )
  }

  "authorize" should "return none if user wasn't found" in new mocks {

    (userDAO.getUser _).when(login).returns(None)

    await(
      authorizationService.authorize(trueCredentials)
    ) shouldBe None

    (sessions.put _).verify(sessionId, time).never()
    (encryptService.encrypt _).verify(password).never()
  }

  "authorize" should "return none if password is wrong" in new mocks {

    (userDAO.getUser _).when(login).returns(Some(user))
    (encryptService.encrypt _).when(password).returns(encryptedPassword)
    (sessionGenerator.generateSession _).when().returns(sessionId)

    await(authorizationService.authorize(falseCredentials)) shouldBe None
    (sessions.put _).verify(sessionId, time).never()
  }

  "authorize" should "put session in the map and return it" in new mocks {

    (userDAO.getUser _).when(login).returns(Some(user))
    (encryptService.encrypt _).when(password).returns(encryptedPassword)
    (sessionGenerator.generateSession _).when().returns(sessionId)

    await(authorizationService.authorize(trueCredentials)) shouldBe Some(wrappedSessionId)
    (sessions.put _).verify(sessionId, time).once()
  }
}
