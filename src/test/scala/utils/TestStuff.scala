package utils

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpecLike, Matchers}

trait TestStuff extends FlatSpecLike with Matchers with MockFactory with ScalatestRouteTest with FutureUtils
