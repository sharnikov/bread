package ru.bread.services

import java.util.Date

import ru.bread.database.OrderStatus.Status
import ru.bread.database._
import ru.bread.errors.AppError.{DatabaseException, VerboseServiceException}
import ru.bread.errors.ErrorCode
import ru.bread.http.Completed
import ru.bread.services.Domain.Id
import ru.bread.settings.schedulers.ServiceContext

import scala.concurrent.Future

trait OrdersService {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
  def getOrderById(userId: Id, orderId: Id): Future[FullOrder]
  def addOrder(order: NewOrder): Future[ResponseWithId]
  def changeStatus(orderId: Id, status: Status): Future[Completed]
  def addItemToOrder(newItem: NewItem): Future[Completed]
}

class OrdersServiceImpl(dao: OrdersDAO, timeProvider: TimeProvider) extends OrdersService with ServiceContext {
  override def getAllGoods(): Future[List[Good]] = dao.getAllGoods()
  override def getGoodsByCategory(category: String): Future[List[Good]] = dao.getGoodsByCategory(category)
  override def getOrderById(userId: Id, orderId: Id): Future[FullOrder] = dao.getOrderById(userId, orderId)

  override def addOrder(newOrder: NewOrder): Future[ResponseWithId] = {
    val order = Order(
      id = None,
      userId = newOrder.userId,
      status = OrderStatus.NEW,
      creationDate = timeProvider.currentTime
    )

    val items = newOrder.packs.map(pack => Item(
      goodId = pack.goodId,
      orderId = None,
      quantity = pack.quantity
    ))

    dao.addOrder(order, items).map(orderId => ResponseWithId(orderId.getOrElse(
      throw new DatabaseException("Could not retrieve orderId")
    )))
  }

  override def changeStatus(orderId: Id, status: Status): Future[Completed] = dao.changeStatus(orderId, status)

  override def addItemToOrder(newItem: NewItem): Future[Completed] = dao.addItemToOrder(newItem.item, newItem.userId)
}
