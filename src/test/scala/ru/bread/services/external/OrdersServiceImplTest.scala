package ru.bread.services.external

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpecLike, Matchers}
import ru.bread.database.services.OrdersDAO
import ru.bread.errors.AppError.DatabaseException
import ru.bread.services.ResponseWithId
import ru.bread.services.internal.FixedTimeProvider
import test.data.OrdersTestData._
import test.data.CommonTestData._
import utils.FutureUtils

class OrdersServiceImplTest extends FlatSpecLike with Matchers with MockFactory with FutureUtils {

  trait mocks {
    val dao = stub[OrdersDAO]
    val timeProvider = new FixedTimeProvider(time)
    val catalogService = new OrdersServiceImpl(dao, timeProvider)
  }

  "addOrder" should "build order and items" in new mocks {
    (dao.addOrder _).when(order, items).returns(Some(orderId))

    await(catalogService.addOrder(userId, listOfGoodsPack)) shouldBe ResponseWithId(orderId)
  }

  "addOrder" should "throw an exception when dao returns no orderId" in new mocks {
    (dao.addOrder _).when(order, items).returns(None)

    awaitFailed[DatabaseException](catalogService.addOrder(userId, listOfGoodsPack)).getMessage shouldBe "Could not retrieve orderId"
  }

  "getOrderById" should "build full order" in new mocks {
    (dao.getOrderById _).when(userId, orderId).returns(orderItems)

    await(catalogService.getOrderById(userId, orderId)) shouldBe fullOrder
  }
}
