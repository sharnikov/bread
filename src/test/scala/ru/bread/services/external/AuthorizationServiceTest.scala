package ru.bread.services.external

import ru.bread.database.services.UserDAO
import ru.bread.errors.AppError.AuthorizationException
import ru.bread.modules.AuthorizationModule.SessionStorage
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
    val sessions = stub[SessionStorage]

    (session.size _).when().returns(30)

    def timeProvider() = new FixedTimeProvider(time)

    val authorizationService = new BasicAuthorizationService(
      userDAO,
      sessionGenerator,
      encryptService,
      timeProvider(),
      sessions
    )
  }

  "authorize" should "return none if user wasn't found" in new mocks {

    (userDAO.getUser _).when(login).returns(None)

    awaitFailed[AuthorizationException](
      authorizationService.authorize(trueCredentials)
    ).getMessage shouldBe "Login or password is incorrect"

    (sessions.put _).verify(sessionId, sessionObj).never()
    (encryptService.encrypt _).verify(password).never()
  }

  "authorize" should "return none if password is wrong" in new mocks {

    (userDAO.getUser _).when(login).returns(Some(user))
    (encryptService.encrypt _).when(password).returns(encryptedPassword)
    (sessionGenerator.generateSession _).when().returns(sessionId)

    awaitFailed[AuthorizationException](
      authorizationService.authorize(falseCredentials)
    ).getMessage shouldBe "Login or password is incorrect"
    (sessions.put _).verify(sessionId, sessionObj).never()
  }

  "authorize" should "put session in the map and return it" in new mocks {

    (userDAO.getUser _).when(login).returns(Some(user))
    (encryptService.encrypt _).when(password).returns(encryptedPassword)
    (sessionGenerator.generateSession _).when().returns(sessionId)

    await(authorizationService.authorize(trueCredentials)) shouldBe Some(wrappedSessionId)
    (sessions.put _).verify(sessionId, sessionObj).once()
  }

  "userBySessionId" should "update valid session and return it" in new mocks {

    override def timeProvider() = new FixedTimeProvider(time2)

    (sessionGenerator.generateSession _).when().returns(sessionId)
    (sessions.get _).when(*).returns(sessionObj)
    (sessions.containsKey _).when(*).returns(true)

    await(authorizationService.userBySessionId(sessionId)) shouldBe user

    (sessions.computeIfPresent _).verify(sessionId, *).once()
  }
}
