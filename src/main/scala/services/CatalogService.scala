package services

import dao.DAO
import domain.Domain.Id
import domain.{FullOrder, Good}

import scala.concurrent.Future

trait CatalogService {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
  def getOrderById(userId: Id, orderId: Id): Future[FullOrder]
}

class CatalogServiceImpl(dao: DAO) extends CatalogService {
  override def getAllGoods(): Future[List[Good]] = dao.getAllGoods()
  override def getGoodsByCategory(category: String): Future[List[Good]] = dao.getGoodsByCategory(category)
  override def getOrderById(userId: Id, orderId: Id): Future[FullOrder] = dao.getOrderById(userId, orderId)
}
