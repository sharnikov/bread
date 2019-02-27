package ru.bread.http.routes

import akka.http.scaladsl.server.Directives.{complete, onComplete}
import com.typesafe.scalalogging.LazyLogging
import spray.json.JsonWriter

import scala.concurrent.Future
import scala.util.{Failure, Success}
import ru.bread.http.response.Response._
import ru.bread.http.response.JsonParsers._

trait RoutesUtils extends LazyLogging {

  def completeResult[T : JsonWriter](result: Future[T]) = {
    onComplete(result) {
      case Success(info) =>
        logger.info("Successful result = {}", info)
        complete(success(info))
      case Failure(exception) => throw exception
    }
  }

}
