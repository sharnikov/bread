import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpecLike, Matchers}
import services.CatalogService
import utils.FutureUtils
import domain.JsonParsers._
import DomainTestData._
import domain.{FullOrder, Good, ResponseWithId}
import org.scalatest.Matchers
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._

class RoutesTest extends FlatSpecLike with Matchers with MockFactory with ScalatestRouteTest with FutureUtils {

  trait mocks {

    val catalogService = stub[CatalogService]
    val routes = new Routes(catalogService).getRoutes()

    (catalogService.getAllGoods _).when().returns(goodsList)
    (catalogService.getOrderById _).when(userId, orderId).returns(DomainTestData.fullOrder)
    (catalogService.getGoodsByCategory _).when(category).returns(goodsList)
    (catalogService.addOrder _).when(DomainTestData.newOrder).returns(ResponseWithId(orderId))
  }

  "all_goods" should "return goods" in new mocks {
    Get("/all_goods") ~> routes ~> check {
      responseAs[List[Good]] shouldEqual goodsList
    }
  }

  "order_by_id" should "return order" in new mocks {
    import domain.JsonParsers.fullOrder
    Get(s"/order_by_id?userId=$userId&orderId=$orderId") ~> routes ~> check {
      responseAs[FullOrder] shouldEqual DomainTestData.fullOrder
    }
  }

  "goods_by_category" should "return goods with this category" in new mocks {
    Get(s"/goods_by_category?category=$category") ~> routes ~> check {
      responseAs[List[Good]] shouldEqual goodsList
    }
  }

  "add_order" should "return id of added order" in new mocks {
    import domain.JsonParsers.newOrder
    Post("/add_order", DomainTestData.newOrder) ~> routes ~> check {
      responseAs[ResponseWithId] shouldBe ResponseWithId(orderId)
    }
  }

}