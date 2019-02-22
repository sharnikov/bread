package services

import database.OrdersDAO
import errors.AppError.VerboseServiceException
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpecLike, Matchers}
import utils.DomainTestData._
import utils.FutureUtils

class CatalogServiceImplTest extends FlatSpecLike with Matchers with MockFactory with FutureUtils {

  trait mocks {
    val dao = stub[OrdersDAO]
    val catalogService = new CatalogServiceImpl(dao)
  }

  "addOrder" should "build order and items" in new mocks {
    (dao.addOrder _).when(order, items).returns(Some(orderId))

    await(catalogService.addOrder(newOrder)) shouldBe ResponseWithId(orderId)
  }

  "addOrder" should "throw an exception when dao returns no orderId" in new mocks {
    (dao.addOrder _).when(order, items).returns(None)

    awaitFailed[VerboseServiceException](catalogService.addOrder(newOrder)).getMessage shouldBe "Could not retrieve orderId"
  }

}
