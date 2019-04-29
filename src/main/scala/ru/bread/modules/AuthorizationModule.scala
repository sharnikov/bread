package ru.bread.modules

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import ru.bread.database.User
import ru.bread.database.services.UserDAO
import ru.bread.modules.AuthorizationModule.{Session, SessionStorage}
import ru.bread.services.external.{AuthorizationService, BasicAuthorizationService}
import ru.bread.services.internal.TimeProvider
import ru.bread.services.security.{EncryptService, SessionGenerator}

class AuthorizationModule(userDao: UserDAO,
                          sessionGenerator: SessionGenerator,
                          encryptService: EncryptService,
                          timeProvider: TimeProvider) extends Module {

  override def name(): String = "Authorization module"

  val sessions: SessionStorage = new ConcurrentHashMap[String, Session]()

  val authorizationService: AuthorizationService = new BasicAuthorizationService(
    userDAO = userDao,
    sessionGenerator = sessionGenerator,
    encryptService = encryptService,
    timeProvider = timeProvider,
    sessions = sessions
  )

}

object AuthorizationModule {
  type SessionStorage = ConcurrentHashMap[String, Session]

  case class Session(user: User, expireDate: Date)
}
