package utils

import java.util.concurrent.TimeUnit

import org.scalatest.Matchers.fail
import org.scalatest.exceptions.TestFailedException

import scala.concurrent.duration.{Duration, FiniteDuration, TimeUnit}
import scala.concurrent.{Await, Future}
import scala.reflect.ClassTag
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

trait FutureUtils {

  implicit protected def toFuture[T](value: T) = Future.successful(value)

  def await[T](future: Future[T]) = Await.result(future, FiniteDuration.apply(5, TimeUnit.SECONDS))

  def awaitFailed[E <: Throwable](future: => Future[_])(implicit ct: ClassTag[E]): E = {
    awaitReady(future).value match {
      case Some(Failure(th: E)) =>
        th

      case Some(Failure(th)) =>
        fail(s"Future passed to awaitFailed was expected to fail with ${ct.runtimeClass}, but failed with ${th.getClass}", th)

      case None =>
        fail("This case is not possible in general since it indicates that Future passed to awaitReady is really not ready here!")

      case Some(Success(value)) =>
        fail("Future passed to awaitFailed succeeded with no Throwable and value: " + value)
    }
  }

  private def awaitReady[T](future: => Future[T]): Future[T] = {
    try {
      val f = future
      Await.ready(f, Duration.Inf)
      f
    } catch {
      case NonFatal(e) => throw new TestFailedException(
        "Value passed to awaitFailed failed synchronously instead of being a failed Future", e, 0)
    }
  }
}
