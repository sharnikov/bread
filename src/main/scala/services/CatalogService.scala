package services

import dao.DAO
import domain.Domain.Id
import domain._
import settings.ServiceContext

import scala.concurrent.Future

trait CatalogService {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
  def getOrderById(userId: Id, orderId: Id): Future[FullOrder]
  def addOrder(order: NewOrder): Future[ResponseWithId]
}

class CatalogServiceImpl(dao: DAO) extends CatalogService with ServiceContext {
  override def getAllGoods(): Future[List[Good]] = dao.getAllGoods()
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

    dao.addOrder(order, items).map(orderId => ResponseWithId(orderId.getOrElse(throw new DBException("Could not retrive orderId"))))
  }
}
