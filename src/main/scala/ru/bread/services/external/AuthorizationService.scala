package ru.bread.services.external

import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.directives.Credentials.Provided
import com.typesafe.scalalogging.LazyLogging
import ru.bread.database.User
import ru.bread.database.services.UserDAO
import ru.bread.errors.AppError.{AuthorizationException, VerboseServiceException}
import ru.bread.errors.ErrorCode
import ru.bread.modules.AuthorizationModule.{Session, SessionStorage}
import ru.bread.services.SessionId
import ru.bread.services.internal.TimeProvider
import ru.bread.services.security.{EncryptService, SessionGenerator}
import ru.bread.settings.schedulers.ServiceContext

import scala.concurrent.Future

trait AuthorizationService {
  def authorize(credential: Credentials): Future[Option[SessionId]]
  def userBySessionId(sessionId: String): User
}

class BasicAuthorizationService(userDAO: UserDAO,
                                sessionGenerator: SessionGenerator,
                                encryptService: EncryptService,
                                timeProvider: TimeProvider,
                                sessions: SessionStorage)
  extends AuthorizationService with ServiceContext with LazyLogging {

  override def authorize(credential: Credentials): Future[Option[SessionId]] = {
    credential match {
      case cred @ Provided(login) =>
        userDAO.getUser(login).map { userOpt =>
          val sessionId = extractSessionId(cred, login, userOpt)
          if (sessionId.isEmpty) {
            logger.error(s"Auth failed for user with login = $login")
            throw new AuthorizationException("Login or password is incorrect")
          }
          sessionId
        }
      case _ =>
        logger.warn(s"No credentials request")
        Future.successful(None)
    }
  }

  private def extractSessionId(cred: Credentials.Provided, login: String, userOpt: Option[User]) = {
    userOpt
      .filter(user => cred.verify(user.password, encryptService.encrypt))
      .map { user =>
        logger.info(s"User with login = $login was authorized")
        val session = Session(
          user = user,
          expireDate = timeProvider.currentTime
        )
        SessionId(getSessionId(session))
      }
  }

  private def getSessionId(session: Session): String = {
    val sessionId = sessionGenerator.generateSession()
    sessions.put(sessionId, session)
    sessionId
  }

  override def userBySessionId(sessionId: String): User =
    updateValidSession(sessionId).user

  private def updateValidSession(sessionId: String): Session = {
    sessions.computeIfPresent(
      sessionId,
      (_: String, oldValue: Session) => oldValue.copy(expireDate = timeProvider.currentTime)
    )
    if (!sessions.containsKey(sessionId))
      throw new VerboseServiceException(ErrorCode.AuthorizationError, "Session is not valid")

    sessions.get(sessionId)
  }
}