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

    val authorizationService = new SimpleAuthorizationService(
      userDAO,
      sessionGenerator,
      encryptService,
      timeProvider,
      sessions
    )
  }

  "login" should "failed when login or password is wrong" in new mocks {

    (userDAO.getUser _).when(login, encryptedPassword).returns(None)
    (encryptService.encrypt _).when(password).returns(encryptedPassword)

    awaitFailed[VerboseServiceException](
      authorizationService.login(login, password)
    ).getMessage shouldBe "Wrong login or password"
    (sessions.put _).verify(sessionId, time).never()
  }

  "login" should "put session in the map and return it" in new mocks {

    (userDAO.getUser _).when(login, encryptedPassword).returns(Some(user))
    (encryptService.encrypt _).when(password).returns(encryptedPassword)
    (sessionGenerator.generateSession _).when().returns(sessionId)

    await(authorizationService.login(login, password)) shouldBe wrappedSessionId
    (sessions.put _).verify(sessionId, time).once()
  }
}
