package ru.bread.http.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, failWith, onComplete}
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging
import spray.json.{DefaultJsonProtocol, RootJsonWriter}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait RoutesUtils extends LazyLogging with DefaultJsonProtocol with SprayJsonSupport {

  def completeResult[T : RootJsonWriter](result: Future[T]): Route = {
    onComplete(result) {
      case Success(info) =>
        logger.info("Successful result = {}", info)
        complete(info)
      case Failure(exception) =>
        logger.warn("Failed result = {}", exception)
        failWith(exception)
    }
  }
}
