package ru.bread.services.external

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import com.typesafe.scalalogging.LazyLogging
import ru.bread.database.services.UserDAO
import ru.bread.errors.AppError.VerboseServiceException
import ru.bread.errors.ErrorCode.AuthorizationError
import ru.bread.services.SessionId
import ru.bread.services.internal.TimeProvider
import ru.bread.settings.config.Settings
import ru.bread.settings.schedulers.ServiceContext

import scala.concurrent.Future
import scala.util.Random

trait AuthorizationService {
  def login(login: String, password: String): Future[SessionId]
}

class SimpleAuthorizationService(userDAO: UserDAO,
                                 timeProvider: TimeProvider,
                                 sessions: ConcurrentHashMap[String, Date],
                                 settings: Settings)
  extends AuthorizationService with ServiceContext with LazyLogging {

  override def login(login: String, password: String): Future[SessionId] = {
    userDAO.getUser(login, password).map { userOpt =>
      userOpt.map(_ => SessionId(updateSession())).getOrElse {
        logger.error(s"User with login $login wasn't found")
        throw new VerboseServiceException(AuthorizationError, "Wrong login or password")
      }
    }
  }

  private def updateSession(sessionId: Option[String] = None): String = {

    def updateSession(currentId: String) = {
      val date = timeProvider.currentTime
      sessions.put(currentId, date)
    }

    sessionId.map { currentId =>
      updateSession(currentId)
      currentId
    }.getOrElse {
      val sessionId = Random.alphanumeric.take(settings.sessionSettings().size()).mkString
      updateSession(sessionId)
      sessionId
    }
  }
}