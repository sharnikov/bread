package ru.bread.modules

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import test.data.OrdersTestData.{category, goodsList, orderId, userId}
import utils.TestStuff
import akka.http.scaladsl.model.StatusCodes
import ru.bread.database.{Good, OrderStatus}
import ru.bread.services.{FullOrder, ResponseWithId}
import ru.bread.http.response.JsonParsers._
import ru.bread.http.response.Completed
import ru.bread.http.response.Response.SuccessfulResponse
import ru.bread.services.external.OrdersService
import test.data.OrdersTestData._

class OrdersModuleTest extends TestStuff {

  trait mocks {
    val dbModule = stub[DatabaseModule]
    val commonModule = stub[CommonModule]
    val catalogService = stub[OrdersService]

    val module = new OrdersModule(dbModule, commonModule, new ConcurrentHashMap[String, Date]())
    val routes = module.routes(catalogService)

    (catalogService.getAllGoods _).when().returns(goodsList)
    (catalogService.getOrderById _).when(userId, orderId).returns(fullOrder)
    (catalogService.getGoodsByCategory _).when(category).returns(goodsList)
    (catalogService.addOrder _).when(newOrder).returns(ResponseWithId(orderId))
    (catalogService.changeStatus _).when(orderId, OrderStatus.REJECTED).returns(Completed)
    (catalogService.addItemToOrder _).when(newItem).returns(Completed)
  }

  "all_goods" should "return goods" in new mocks {
    Get("/all_goods") ~> routes ~> check {
      responseAs[SuccessfulResponse[List[Good]]].payload shouldEqual goodsList
    }
  }

  "order_by_id" should "return order" in new mocks {
    Get(s"/order_by_id?userId=$userId&orderId=$orderId") ~> routes ~> check {
      val a = responseAs[SuccessfulResponse[FullOrder]].payload
      a shouldEqual fullOrder
    }
  }

  "goods_by_category" should "return goods with this category" in new mocks {
    Get(s"/goods_by_category?category=$category") ~> routes ~> check {
      responseAs[SuccessfulResponse[List[Good]]].payload shouldEqual goodsList
    }
  }

  "add_order" should "return id of added order" in new mocks {
    Post("/add_order", newOrder) ~> routes ~> check {
      responseAs[SuccessfulResponse[ResponseWithId]].payload shouldBe ResponseWithId(orderId)
    }
  }

  "add_item" should "add a new item" in new mocks {
    Post("/add_item", newItem) ~> routes ~> check {

      status shouldBe StatusCodes.OK
    }
  }

}
