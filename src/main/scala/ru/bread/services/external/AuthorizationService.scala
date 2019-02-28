package ru.bread.services.external

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.directives.Credentials.Provided
import com.typesafe.scalalogging.LazyLogging
import ru.bread.database.services.UserDAO
import ru.bread.errors.AppError.VerboseServiceException
import ru.bread.errors.ErrorCode
import ru.bread.services.SessionId
import ru.bread.services.internal.{EncryptService, SessionGenerator, TimeProvider}
import ru.bread.settings.schedulers.ServiceContext

import scala.concurrent.Future

trait AuthorizationService {
  def authorize(credential: Credentials): Future[Option[SessionId]]
}

class BasicAuthorizationService(userDAO: UserDAO,
                                sessionGenerator: SessionGenerator,
                                encryptService: EncryptService,
                                timeProvider: TimeProvider,
                                sessions: ConcurrentHashMap[String, Date])
  extends AuthorizationService with ServiceContext with LazyLogging {

  override def authorize(credential: Credentials): Future[Option[SessionId]] = {
    credential match {
      case cred @ Provided(login) =>
        userDAO.getUser(login).map { userOpt =>
          userOpt
            .filter(user => cred.verify(user.password, encryptService.encrypt))
            .map {_ =>
              logger.info(s"User with login = $login was authorized")
              SessionId(getSession())
            }
        }
      case _ =>
        logger.info(s"No credentials request")
        Future.successful(None)
    }
  }

  private def getSession(): String = {
    val sessionId = sessionGenerator.generateSession()
    updateSession(sessionId)
    sessionId
  }

  private def updateSession(sessionId: String) = {
    val date = timeProvider.currentTime
    sessions.put(sessionId, date)
  }

  private def updateValidSession(sessionId: String) = {
    if (sessions.contains(sessionId)) updateSession(sessionId)
    else throw new VerboseServiceException(ErrorCode.AuthorizationError, "Session is not valid")
  }
}