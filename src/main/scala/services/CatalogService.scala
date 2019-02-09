package services

import dao.DAO
import domain._
import domain.Domain.Id
import domain.OrderStatus.Status
import settings.ServiceContext
import errors.AppError.VerboseServiceException
import errors.ErrorCode

import scala.concurrent.Future

trait CatalogService {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
  def getOrderById(userId: Id, orderId: Id): Future[FullOrder]
  def addOrder(order: NewOrder): Future[ResponseWithId]
  def changeStatus(orderId: Id, status: Status): Future[Unit]
}

class CatalogServiceImpl(dao: DAO) extends CatalogService with ServiceContext {
  override def getAllGoods(): Future[List[Good]] = {
    dao.getAllGoods()//.map(_ => throw new VerboseServiceException(ErrorCode.DataNotFound,"Could not retrieve orderId"))
  }
  override def getGoodsByCategory(category: String): Future[List[Good]] = dao.getGoodsByCategory(category)
  override def getOrderById(userId: Id, orderId: Id): Future[FullOrder] = dao.getOrderById(userId, orderId)

  override def addOrder(fullOrder: NewOrder): Future[ResponseWithId] = {
    val order = Order(
      id = None,
      userId = fullOrder.userId,
      status = OrderStatus.NEW
    )

    val items = fullOrder.packs.map(pack => Item(
      goodId = pack.goodId,
      orderId = None,
      quantity = pack.quantity
    ))

    dao.addOrder(order, items).map(orderId => ResponseWithId(orderId.getOrElse(
      throw new VerboseServiceException(ErrorCode.DataNotFound,"Could not retrieve orderId")
    )))
  }

  override def changeStatus(orderId: Id, status: Status): Future[Unit] = dao.changeStatus(orderId, status)
}
