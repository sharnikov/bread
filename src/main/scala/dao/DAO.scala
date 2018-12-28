package dao

import domain.Domain.Id
import domain._
import settings.DBContext
import settings.DatabaseFactory.DbContext

import scala.concurrent.Future

trait DAO {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
  def getOrderById(userId: Id, orderId: Id): Future[FullOrder]
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

  override def getOrderById(userId: Id, orderId: Id): Future[FullOrder] = {
    run(quote {
      querySchema[Order]("orders", _.id -> "id", _.userId -> "user_id").filter(_.userId == lift(userId))
        .join(querySchema[Item]("items", _.quantity -> "quantity", _.goodId -> "good_id", _.orderId -> "order_id")).on((order, item) => order.id == item.orderId)
        .map { case (_, item) => item }
        .join(querySchema[Good]("goods")).on((item, good) => item.goodId == good.id)
        .map { case (item, good) => GoodsPack(item.quantity, good) }
    }).map { goodsWithQuantity =>
      FullOrder(
        userId = userId,
        orderId = orderId,
        goods = goodsWithQuantity//.map { case (q, g) => GoodsPack(q,g)}
      )
    }
  }
}
