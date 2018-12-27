package services

import domain.Good

import scala.concurrent.Future

trait CatalogService {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
}

class CatalogServiceImpl(dao: DAO) extends CatalogService {
  override def getAllGoods(): Future[List[Good]] = dao.getAllGoods()
  override def getGoodsByCategory(category: String): Future[List[Good]] = dao.getGoodsByCategory(category)
}
