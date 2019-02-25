package http

import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.ExceptionHandler
import com.typesafe.scalalogging.LazyLogging
import settings.JsonParsers._
import http.Response._
import http.Response.fail
import errors.AppError.{ServiceException, VerboseServiceException}

trait RoutesSettings extends LazyLogging {

  implicit def exceptionHandler = ExceptionHandler {
    case exception: VerboseServiceException =>
      logger.info("Failed with a verboseException", exception)
      complete(fail(exception))
    case exception =>
      logger.info("Failed with an exception", exception)
      complete(fail(new ServiceException("Internal exception", exception)))
  }

}
