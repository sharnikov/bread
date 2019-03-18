package ru.bread.services.external

import ru.bread.database.OrderStatus.Status
import ru.bread.database._
import ru.bread.database.services.OrdersDAO
import ru.bread.errors.AppError.DatabaseDataNotFoundException
import ru.bread.http.response.Completed
import ru.bread.services.Domain.Id
import ru.bread.services._
import ru.bread.services.internal.TimeProvider
import ru.bread.settings.schedulers.ServiceContext

import scala.concurrent.Future

trait OrdersService {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
  def getOrderById(userId: Id, orderId: Id): Future[FullOrder]
  def addOrder(userId: Id, goodsPack: Seq[GoodsPack]): Future[ResponseWithId]
  def changeStatus(orderId: Id, status: Status): Future[Completed]
  def addItemToOrder(userId: Id, newItem: Item): Future[Completed]
  def removeItemFromOrder(userId: Id, item: Item): Future[Completed]
}

class OrdersServiceImpl(dao: OrdersDAO, timeProvider: TimeProvider) extends OrdersService with ServiceContext {
  override def getAllGoods(): Future[List[Good]] = dao.getAllGoods()
  override def getGoodsByCategory(category: String): Future[List[Good]] = dao.getGoodsByCategory(category)
  override def getOrderById(userId: Id, orderId: Id): Future[FullOrder] =
    dao.getOrderById(userId, orderId).map { orderItems =>
      FullOrder(
        userId = userId,
        id = orderId,
        packs = orderItems.packs,
        creationDate = orderItems.creationDate,
        totalPrice = orderItems.packs.map(pack => pack.good.price * pack.quantity).sum
      )
    }

  override def addOrder(userId: Id, goodsPack: Seq[GoodsPack]): Future[ResponseWithId] = {
    val order = Order(
      id = None,
      userId = userId,
      status = OrderStatus.NEW,
      creationDate = timeProvider.currentTime
    )

    val items = goodsPack.map(pack => Item(
      goodId = pack.goodId,
      orderId = None,
      quantity = pack.quantity
    ))

    dao.addOrder(order, items).map(orderId => ResponseWithId(orderId.getOrElse(
      throw new DatabaseDataNotFoundException("Could not retrieve orderId")
    )))
  }

  override def changeStatus(orderId: Id, status: Status): Future[Completed] =
    dao.changeStatus(orderId, status)

  override def addItemToOrder(userId: Id, item: Item): Future[Completed] =
    dao.addItemToOrder(userId, item)

  override def removeItemFromOrder(userId: Id, item: Item): Future[Completed] =
    dao.removeItemFromOrder(userId, item)
}
