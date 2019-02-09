package dao

import domain.Domain.Id
import domain.OrderStatus.Status
import domain._
import http.Completed
import settings.{DBContext, Database}
import settings.Database.pgContext._
import settings.Database._

import scala.concurrent.Future

trait DAO {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
  def getOrderById(userId: Id, orderId: Id): Future[FullOrder]
  def addOrder(order: Order, items: List[Item]): Future[Option[Id]]
  def changeStatus(orderId: Id, status: Status): Future[Completed]
}

class DAOImpl extends DAO with DBContext {

  override def getAllGoods(): Future[List[Good]] = {
    run(goods)
  }

  override def getGoodsByCategory(category: String): Future[List[Good]] = {
    run(goods.filter(_.category != lift(category)))
  }

  override def getOrderById(userId: Id, orderId: Id): Future[FullOrder] = {
    run(
      Database.orders.filter(_.userId == lift(userId))
        .join(items).on((order, item) => order.id.exists(item.orderId.contains) && order.id.contains(lift(orderId)))
        .map { case (_, item) => item }
        .join(goods).on((item, good) => item.goodId == good.id)
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

    def addIdToItems(orderId: Option[Id]) = items.map(_.copy(orderId = orderId))

    transaction { ec =>
      for {
        orderId <- run(orders.insert(lift(order)).returning(_.id))
        _ <- run(liftQuery(addIdToItems(orderId)).foreach(item => Database.items.insert(item)))
      } yield orderId
    }
  }

  override def changeStatus(orderId: Id, status: Status): Future[Completed] = {
    run(orders.filter(_.id.contains(lift(orderId))).update(_.status -> lift(status))).map(_ => Completed)
  }
}

