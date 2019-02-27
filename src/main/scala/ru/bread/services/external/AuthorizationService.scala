package ru.bread.services.external

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import com.typesafe.scalalogging.LazyLogging
import ru.bread.database.services.UserDAO
import ru.bread.errors.AppError.VerboseServiceException
import ru.bread.errors.ErrorCode
import ru.bread.errors.ErrorCode.AuthorizationError
import ru.bread.services.SessionId
import ru.bread.services.internal.{EncryptService, SessionGenerator, TimeProvider}
import ru.bread.settings.schedulers.ServiceContext

import scala.concurrent.Future

trait AuthorizationService {
  def login(login: String, password: String): Future[SessionId]
}

class SimpleAuthorizationService(userDAO: UserDAO,
                                 sessionGenerator: SessionGenerator,
                                 encryptService: EncryptService,
                                 timeProvider: TimeProvider,
                                 sessions: ConcurrentHashMap[String, Date])
  extends AuthorizationService with ServiceContext with LazyLogging {

  override def login(login: String, password: String): Future[SessionId] =
    userDAO.getUser(login, encryptService.encrypt(password)).map { userOpt =>
      userOpt.map(_ => SessionId(getSession())).getOrElse {
        logger.error(s"User with login = $login wasn't found")
        throw new VerboseServiceException(AuthorizationError, "Wrong login or password")
      }
    }

  private def updateValidSession(sessionId: String) = {
    if (sessions.contains(sessionId)) updateSession(sessionId)
    else throw new VerboseServiceException(ErrorCode.AuthorizationError, "Session is not valid")
  }

  private def getSession(sessionId: Option[String] = None): String = {
    val sessionId = sessionGenerator.generateSession()
    updateSession(sessionId)
    sessionId
  }

  private def updateSession(sessionId: String) = {
    val date = timeProvider.currentTime
    sessions.put(sessionId, date)
  }
}