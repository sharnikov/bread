package ru.bread.http.routes

import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.ExceptionHandler
import com.typesafe.scalalogging.LazyLogging
import ru.bread.errors.AppError.{DatabaseException, ServiceException, VerboseServiceException}
import ru.bread.http.response.Response._
import ru.bread.http.response.JsonParsers._
import ru.bread.errors.ErrorCode

trait RoutesSettings extends LazyLogging {

  implicit def exceptionHandler = ExceptionHandler {
    case exception: VerboseServiceException =>
      logger.error(s"Failed with a verboseException with a message: ${exception.getMessage}", exception)
      complete(fail(exception))
    case exception: DatabaseException if exception.code == ErrorCode.DataNotFoundError =>
      logger.error(s"Unable to find a data in the DB with a message: ${exception.getMessage}", exception)
      complete(fail(exception))
    case exception =>
      logger.error(s"Failed with an exception with a message: ${exception.getMessage}", exception)
      complete(fail(new ServiceException("Internal exception", exception)))
  }

}
