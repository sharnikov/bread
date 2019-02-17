package database

import domain.Domain.Id
import domain.OrderStatus.Status
import domain._
import errors.AppError.{BreadException, DatabaseException}
import errors.ErrorCode
import http.Completed
import settings.DBContext
import Database.pgContext._
import Database._

import scala.concurrent.Future

trait DAO {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
  def getOrderById(userId: Id, orderId: Id): Future[FullOrder]
  def addOrder(order: Order, items: List[Item]): Future[Option[Id]]
  def changeStatus(orderId: Id, status: Status): Future[Completed]
  def addItemToOrder(item: Item, userId: Id): Future[Completed]
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

    transaction { _ =>
      for {
        orderId <- run(orders.insert(lift(order)).returning(_.id))
        _ <- run(liftQuery(addIdToItems(orderId)).foreach(item => Database.items.insert(item)))
      } yield orderId
    }
  }

  override def changeStatus(orderId: Id, status: Status): Future[Completed] = {
    transaction( _ => run(orders.filter(_.id.contains(lift(orderId))).update(_.status -> lift(status))).map(_ => Completed))
  }

  override def addItemToOrder(newItem: Item, userId: Id): Future[Completed] = {
    transaction { _ =>
      for {
        orders <- run(orders.filter(order =>
          lift(newItem).orderId.exists(order.id.contains) && lift(userId) == order.userId)
        )
        order = orders.headOption
        _ = if (order.isEmpty) throw new DatabaseException(s"There is no such an order")
        _ = if (order.exists(_.status != OrderStatus.NEW))
          throw new BreadException(ErrorCode.DataNotFound, s"Order status is ${orders.headOption.map(_.status)}, but must be NEW")
        currentItems <- run(items.filter { item =>
          val liftedNewItem = lift(newItem)
          item.goodId == liftedNewItem.goodId && item.orderId.exists(liftedNewItem.orderId.contains)
        })
        _ <- if (currentItems.nonEmpty) {
          val currentItem = currentItems.head
          run(items.update(_.quantity -> lift(currentItem.quantity + newItem.quantity)))
        } else run(items.insert(lift(newItem)))
      } yield Completed
    }
  }
}

