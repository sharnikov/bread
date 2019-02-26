package services

import database.OrdersDAO
import errors.AppError.DatabaseException
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpecLike, Matchers}
import test.data.OrdersTestData._
import utils.FutureUtils

class OrdersServiceImplTest extends FlatSpecLike with Matchers with MockFactory with FutureUtils {

  trait mocks {
    val dao = stub[OrdersDAO]
    val timeProvider = new FixedTimeProvider(time)
    val catalogService = new OrdersServiceImpl(dao, timeProvider)
  }

  "addOrder" should "build order and items" in new mocks {
    (dao.addOrder _).when(order, items).returns(Some(orderId))

    await(catalogService.addOrder(newOrder)) shouldBe ResponseWithId(orderId)
  }

  "addOrder" should "throw an exception when dao returns no orderId" in new mocks {
    (dao.addOrder _).when(order, items).returns(None)

    awaitFailed[DatabaseException](catalogService.addOrder(newOrder)).getMessage shouldBe "Could not retrieve orderId"
  }

}
