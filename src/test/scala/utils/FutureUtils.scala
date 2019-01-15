package utils

import scala.concurrent.Future

trait FutureUtils {

  implicit protected def toFuture[T](value: T) = Future.successful(value)
}
