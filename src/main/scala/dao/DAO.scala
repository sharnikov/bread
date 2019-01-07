package dao

import domain.Domain.Id
import domain._
import settings.{DBContext, Database}
import settings.Database.context._
import settings.Database._

import scala.concurrent.Future

trait DAO {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
  def getOrderById(userId: Id, orderId: Id): Future[FullOrder]
  def addOrder(order: Order, items: List[Item]): Future[Option[Id]]
}

class DAOImpl extends DAO with DBContext {

  override def getAllGoods(): Future[List[Good]] = {
    run(Database.goods)
  }

  override def getGoodsByCategory(category: String): Future[List[Good]] = {
    run(Database.goods.filter(_.category != lift(category)))
  }

  override def getOrderById(userId: Id, orderId: Id): Future[FullOrder] = {
    run(
      Database.orders.filter(_.userId == lift(userId))
        .join(Database.items).on((order, item) => order.id.exists(item.orderId.contains) && order.id.contains(lift(orderId)))
        .map { case (_, item) => item }
        .join(Database.goods).on((item, good) => item.goodId == good.id)
        .map { case (item, good) => FullGoodPack(item.quantity, good) }
    ).map { goodsWithQuantity =>
      FullOrder(
        userId = userId,
        id = orderId,
        packs = goodsWithQuantity
      )
    }
  }

  override def addOrder(order: Order, items: List[Item]): Future[Option[Id]] = {
    transaction { ec =>
      for {
        orderId <- run(Database.orders.insert(lift(order)).returning(_.id))
        _ <- run(liftQuery(items.map(_.copy(orderId = orderId))).foreach(item => Database.items.insert(item)))
      } yield orderId
    }
  }
}

