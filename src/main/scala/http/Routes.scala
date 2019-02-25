package http

import akka.http.scaladsl.server.Directives.{complete, onComplete}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.typesafe.scalalogging.LazyLogging
import spray.json.JsonWriter
import settings.JsonParsers._
import http.Response._
import errors.AppError.{ServiceException, VerboseServiceException}
import http.Response.fail

import scala.concurrent.Future
import scala.util.Success

trait Routes extends LazyLogging {

  def routes(): Route

  def completeResult[T : JsonWriter](result: Future[T]) = {
    import http.Response._
    onComplete(result) {
      case Success(info) =>
        logger.info("Successful result = {}", info)
        complete(success(info))
    }
  }

  implicit def exceptionHandler = ExceptionHandler {
    case exception: VerboseServiceException =>
      logger.info("Failed with a verboseException", exception)
      complete(fail(exception))
    case exception =>
      logger.info("Failed with an exception", exception)
      complete(fail(new ServiceException("Internal exception", exception)))
  }
}
