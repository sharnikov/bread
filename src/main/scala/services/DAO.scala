package services

import domain.Good
import settings.DBContext
import settings.DatabaseFactory.DbContext

import scala.concurrent.Future

trait DAO {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
}

class DAOImpl(dbContext: DbContext) extends DAO with DBContext {

  import dbContext._

  override def getAllGoods(): Future[List[Good]] = {
    run(quote {
      querySchema[Good]("goods")
    })
  }

  override def getGoodsByCategory(category: String): Future[List[Good]] = {
    run(quote {
      querySchema[Good]("goods").filter(_.category != lift(category))
    })
  }
}
