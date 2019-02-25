package modules

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import test.data.OrdersTestData.{category, goodsList, orderId, userId}
import utils.TestStuff
import akka.http.scaladsl.model.StatusCodes
import database.{Good, OrderStatus, OrdersDAO}
import services.{OrdersService, FullOrder, ResponseWithId}
import settings.JsonParsers._
import http.Completed
import http.Response._
import test.data.OrdersTestData

class OrdersModuleTest extends TestStuff {

  trait mocks {
    val dbModule = stub[DatabaseModule]
    val module = new OrdersModule(dbModule, new ConcurrentHashMap[String, Date]())
    val catalogService = stub[OrdersService]
    val routes = module.routes(catalogService)

    (catalogService.getAllGoods _).when().returns(goodsList)
    (catalogService.getOrderById _).when(userId, orderId).returns(OrdersTestData.fullOrder)
    (catalogService.getGoodsByCategory _).when(category).returns(goodsList)
    (catalogService.addOrder _).when(OrdersTestData.newOrder).returns(ResponseWithId(orderId))
    (catalogService.changeStatus _).when(orderId, OrderStatus.REJECTED).returns(Completed)
    (catalogService.addItemToOrder _).when(OrdersTestData.newItem).returns(Completed)
  }

  "all_goods" should "return goods" in new mocks {
    Get("/all_goods") ~> routes ~> check {
      responseAs[SuccessfulResponse[List[Good]]].payload shouldEqual goodsList
    }
  }

  "order_by_id" should "return order" in new mocks {
    Get(s"/order_by_id?userId=$userId&orderId=$orderId") ~> routes ~> check {
      responseAs[SuccessfulResponse[FullOrder]].payload shouldEqual OrdersTestData.fullOrder
    }
  }

  "goods_by_category" should "return goods with this category" in new mocks {
    Get(s"/goods_by_category?category=$category") ~> routes ~> check {
      responseAs[SuccessfulResponse[List[Good]]].payload shouldEqual goodsList
    }
  }

  "add_order" should "return id of added order" in new mocks {
    Post("/add_order", OrdersTestData.newOrder) ~> routes ~> check {
      responseAs[SuccessfulResponse[ResponseWithId]].payload shouldBe ResponseWithId(orderId)
    }
  }

  "add_item" should "add a new item" in new mocks {
    Post("/add_item", OrdersTestData.newItem) ~> routes ~> check {

      status shouldBe StatusCodes.OK
    }
  }

}
