import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpecLike, Matchers}
import services.CatalogService
import utils.FutureUtils
import domain.JsonParsers._
import DomainTest._
import domain.Good
import org.scalatest.Matchers
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import scala.concurrent.duration._


class RoutesTest extends FlatSpecLike with Matchers with MockFactory with ScalatestRouteTest with FutureUtils {

  trait mocks {

        val catalogService = stub[CatalogService]
        val routes = new Routes(catalogService).getRoutes()

        (catalogService.getAllGoods _).when().returns(goodsList)
  }

  "ALL-GOODS" should "return goods" in new mocks {
        Get("all_goods") ~> routes ~> check {

//          response.entity.dataByte

//          val a = responseAs[List[Good]]

//          responseAs[List[Good]] shouldEqual goodsList
        }
  }

}