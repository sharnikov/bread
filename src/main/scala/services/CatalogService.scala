package services

import domain.Good

import scala.concurrent.Future

trait CatalogService {
  def getAllGoods(): Future[List[Good]]
}

class CatalogServiceImpl extends CatalogService {
  override def getAllGoods(): Future[List[Good]] = Future.successful(List(
    Good("Булка", 45),
    Good("Кекс", 60)
  ))
}
