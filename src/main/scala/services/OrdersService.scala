package services

import database.OrderStatus.Status
import database._
import errors.AppError.VerboseServiceException
import errors.ErrorCode
import http.Completed
import services.Domain.Id
import settings.schedulers.ServiceContext

import scala.concurrent.Future

trait OrdersService {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
  def getOrderById(userId: Id, orderId: Id): Future[FullOrder]
  def addOrder(order: NewOrder): Future[ResponseWithId]
  def changeStatus(orderId: Id, status: Status): Future[Completed]
  def addItemToOrder(newItem: NewItem): Future[Completed]
}

class OrdersServiceImpl(dao: OrdersDAO) extends OrdersService with ServiceContext {
  override def getAllGoods(): Future[List[Good]] = dao.getAllGoods()
  override def getGoodsByCategory(category: String): Future[List[Good]] = dao.getGoodsByCategory(category)
  override def getOrderById(userId: Id, orderId: Id): Future[FullOrder] = dao.getOrderById(userId, orderId)

  override def addOrder(newOrder: NewOrder): Future[ResponseWithId] = {
    val order = Order(
      id = None,
      userId = newOrder.userId,
      status = OrderStatus.NEW
    )

    val items = newOrder.packs.map(pack => Item(
      goodId = pack.goodId,
      orderId = None,
      quantity = pack.quantity
    ))

    dao.addOrder(order, items).map(orderId => ResponseWithId(orderId.getOrElse(
      throw new VerboseServiceException(ErrorCode.DataNotFound, "Could not retrieve orderId")
    )))
  }

  override def changeStatus(orderId: Id, status: Status): Future[Completed] = dao.changeStatus(orderId, status)

  override def addItemToOrder(newItem: NewItem): Future[Completed] = dao.addItemToOrder(newItem.item, newItem.userId)
}
