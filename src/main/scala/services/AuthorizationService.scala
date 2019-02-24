package services

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import com.typesafe.scalalogging.LazyLogging
import database.UserDAO
import errors.AppError.VerboseServiceException
import errors.ErrorCode.DataNotFound
import settings.ServiceContext

import scala.concurrent.Future

trait AuthorizationService {
  def login(login: String, password: String): Future[String]
}

class SimpleAuthorizationService(userDAO: UserDAO, sessions: ConcurrentHashMap[String, Date])
  extends AuthorizationService with ServiceContext with LazyLogging {

  override def login(login: String, password: String): Future[String] = {
    userDAO.getUser(login, password).map { userOpt =>
      userOpt.map(_ => updateSession()).getOrElse {
        logger.error(s"User with login $login wasn't found")
        throw new VerboseServiceException(DataNotFound, "Wrong login or password")
      }
    }
  }

  private def updateSession(sessionId: Option[String] = None): String = {

    def updateSession(currentId: String) = {
      val date = new Date(System.currentTimeMillis())
      sessions.put(currentId, date)
    }

    sessionId.map { currentId =>
      updateSession(currentId)
      currentId
    }.getOrElse {
      val sessionId = scala.util.Random.alphanumeric.take(30).mkString
      updateSession(sessionId)
      sessionId
    }
  }
}