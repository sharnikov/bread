package http

import akka.http.scaladsl.server.Directives.{complete, onComplete}
import com.typesafe.scalalogging.LazyLogging
import spray.json.JsonWriter
import settings.JsonParsers._

import scala.concurrent.Future
import scala.util.Success

trait RoutesUtils extends LazyLogging {

  def completeResult[T : JsonWriter](result: Future[T]) = {
    import http.Response._
    onComplete(result) {
      case Success(info) =>
        logger.info("Successful result = {}", info)
        complete(success(info))
    }
  }

}
