package services

import java.util.concurrent.ExecutorService

import domain.Good
import settings.DBContext
import settings.DatabaseFactory.DbContext

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

trait DAO {
  def getAllGoods(): Future[List[Good]]
}

class DAOImpl(dbContext: DbContext) extends DAO with DBContext {

  import dbContext._

//  private val pool = java.util.concurrent.Executors.newWorkStealingPool()
//  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(pool)

  override def getAllGoods(): Future[List[Good]] = {
    run(quote {
      querySchema[Good]("Goods")
    })
  }

}
