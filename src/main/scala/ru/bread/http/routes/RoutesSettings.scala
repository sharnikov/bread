package ru.bread.http.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.typesafe.scalalogging.LazyLogging
import ru.bread.errors.AppError.{BreadException, VerboseServiceException}
import ru.bread.errors.ErrorCode

trait RoutesSettings extends LazyLogging {

  implicit def exceptionHandler = ExceptionHandler {
    case exception: VerboseServiceException =>
      logger.error(s"Failed with a verboseException with a message: ${exception.getMessage}", exception)
      completeWithResponse(BadRequest, s"Failed with a verboseException with a message: ${exception.getMessage}")
    case exception: BreadException if exception.code == ErrorCode.DataNotFoundError =>
      logger.error(s"Unable to find a data. Exception message: ${exception.getMessage}", exception)
      completeWithResponse(InternalServerError, "Internal exception")
    case exception: BreadException if exception.code == ErrorCode.AuthorizationError =>
      logger.error(s"Authorization exception with a message: ${exception.getMessage}", exception)
      completeWithResponse(Unauthorized, "Login or password is incorrect")
    case exception =>
      logger.error(s"Failed with an exception with a message: ${exception.getMessage}", exception)
      completeWithResponse(InternalServerError, "Internal exception")
  }

  private def completeWithResponse(code: StatusCode, entity: ResponseEntity): Route =
    complete(HttpResponse(code, entity = entity))

}
