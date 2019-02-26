package utils

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpecLike, Matchers}
import ru.bread.settings.config.TestSettings

trait TestStuff extends FlatSpecLike
  with Matchers
  with MockFactory
  with ScalatestRouteTest
  with FutureUtils
  with TestSettings
