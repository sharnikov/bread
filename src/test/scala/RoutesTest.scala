import akka.http.scaladsl.model.StatusCodes
import domain.{FullOrder, Good, OrderStatus, ResponseWithId}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpecLike
import org.scalatest.Matchers
import services.CatalogService
import utils.{DomainTestData, FutureUtils}
import domain.JsonParsers._
import http.Completed
import http.Response._
import utils.DomainTestData._

class RoutesTest extends FlatSpecLike with Matchers with MockFactory with ScalatestRouteTest with FutureUtils {

  trait mocks {

    val catalogService = stub[CatalogService]
    val routes = new Routes(catalogService).getRoutes()

    (catalogService.getAllGoods _).when().returns(goodsList)
    (catalogService.getOrderById _).when(userId, orderId).returns(DomainTestData.fullOrder)
    (catalogService.getGoodsByCategory _).when(category).returns(goodsList)
    (catalogService.addOrder _).when(DomainTestData.newOrder).returns(ResponseWithId(orderId))
    (catalogService.changeStatus _).when(orderId, OrderStatus.REJECTED).returns(Completed)
    (catalogService.addItemToOrder _).when(DomainTestData.newItem).returns(Completed)
  }

  "all_goods" should "return goods" in new mocks {
    Get("/all_goods") ~> routes ~> check {
      responseAs[SuccessfulResponse[List[Good]]].payload shouldEqual goodsList
    }
  }

  "order_by_id" should "return order" in new mocks {
    import domain.JsonParsers.fullOrder
    Get(s"/order_by_id?userId=$userId&orderId=$orderId") ~> routes ~> check {
      responseAs[SuccessfulResponse[FullOrder]].payload shouldEqual DomainTestData.fullOrder
    }
  }

  "goods_by_category" should "return goods with this category" in new mocks {
    Get(s"/goods_by_category?category=$category") ~> routes ~> check {
      responseAs[SuccessfulResponse[List[Good]]].payload shouldEqual goodsList
    }
  }

  "add_order" should "return id of added order" in new mocks {
    import domain.JsonParsers.newOrder
    Post("/add_order", DomainTestData.newOrder) ~> routes ~> check {
      responseAs[SuccessfulResponse[ResponseWithId]].payload shouldBe ResponseWithId(orderId)
    }
  }

  "add_item" should "add a new item" in new mocks {
    import domain.JsonParsers.newItem
    Post("/add_item", DomainTestData.newItem) ~> routes ~> check {

      status shouldBe StatusCodes.OK
    }
  }

}