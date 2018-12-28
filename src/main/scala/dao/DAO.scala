package dao

import domain.Domain.Id
import domain._
import settings.{DBContext, Database}

import scala.concurrent.Future

trait DAO {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
  def getOrderById(userId: Id, orderId: Id): Future[FullOrder]
}

class DAOImpl(database: Database) extends DAO with DBContext {

  import database.context._

  override def getAllGoods(): Future[List[Good]] = {
    run(database.goods)
  }

  override def getGoodsByCategory(category: String): Future[List[Good]] = {
    run(quote {
      database.goods.filter(_.category != lift(category))
    })
  }

  override def getOrderById(userId: Id, orderId: Id): Future[FullOrder] = {
    run(quote {
      database.orders.filter(_.userId == lift(userId))
        .join(database.items).on((order, item) => order.id == item.orderId)
        .map { case (_, item) => item }
        .join(database.goods).on((item, good) => item.goodId == good.id)
        .map { case (item, good) => GoodsPack(item.quantity, good) }
    }).map { goodsWithQuantity =>
      FullOrder(
        userId = userId,
        orderId = orderId,
        goods = goodsWithQuantity
      )
    }
  }
}
