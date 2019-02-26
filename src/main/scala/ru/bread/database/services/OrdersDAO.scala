package ru.bread.database.services

import ru.bread.database.OrderStatus.Status
import ru.bread.database._
import ru.bread.database.settings.PostgresSchema
import ru.bread.errors.AppError.DatabaseException
import ru.bread.http.response.Completed
import ru.bread.services.Domain.Id
import ru.bread.services.{FullGoodPack, FullOrder}
import ru.bread.settings.schedulers.DatabaseContext

import scala.concurrent.Future

trait OrdersDAO {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
  def getOrderById(userId: Id, orderId: Id): Future[FullOrder]
  def addOrder(order: Order, items: List[Item]): Future[Option[Id]]
  def changeStatus(orderId: Id, status: Status): Future[Completed]
  def addItemToOrder(item: Item, userId: Id): Future[Completed]
}

class OrdersDAOImpl(schema: PostgresSchema) extends OrdersDAO with DatabaseContext {

  import schema._
  import schema.dbContext._

  override def getAllGoods(): Future[List[Good]] = {
    run(schema.goods)
  }

  override def getGoodsByCategory(category: String): Future[List[Good]] = {
    run(schema.goods.filter(_.category != lift(category)))
  }

  override def getOrderById(userId: Id, orderId: Id): Future[FullOrder] =
    for {
      orders <- run(orders.filter(order => order.userId == lift(userId) && order.id.contains(lift(orderId))))
      order = orders.headOption.getOrElse(
        throw new DatabaseException(s"There is no order with such an orderId = $orderId")
      )
      goodsWithQuantity <- run(
        items.filter(item =>  item.orderId.contains(lift(orderId)))
          .join(goods).on((item, good) => item.goodId == good.id)
          .map { case (item, good) => FullGoodPack(item.quantity, good) }
      )
    } yield {
      FullOrder(
        userId = userId,
        id = orderId,
        packs = goodsWithQuantity,
        creationDate = order.creationDate
      )
    }

  override def addOrder(order: Order, items: List[Item]): Future[Option[Id]] = {

    def addIdToItems(orderId: Option[Id]) = items.map(_.copy(orderId = orderId))

    transaction { _ =>
      for {
        orderId <- run(schema.orders.insert(lift(order)).returning(_.id))
        _ <- run(liftQuery(addIdToItems(orderId)).foreach(item => schema.items.insert(item)))
      } yield orderId
    }
  }

  override def changeStatus(orderId: Id, status: Status): Future[Completed] = {
    transaction( _ => run(schema.orders.filter(_.id.contains(lift(orderId))).update(_.status -> lift(status))).map(_ => Completed))
  }

  override def addItemToOrder(newItem: Item, userId: Id): Future[Completed] = {
    transaction { _ =>
      for {
        orders <- run(schema.orders.filter(order =>
          lift(newItem).orderId.exists(order.id.contains) && lift(userId) == order.userId)
        )
        order = orders.headOption
        _ = if (order.isEmpty) throw new DatabaseException(s"There is no such an order")
        _ = if (order.exists(_.status != OrderStatus.NEW))
          throw new DatabaseException(s"Order status is ${orders.headOption.map(_.status)}, but must be NEW")
        currentItems <- run(schema.items.filter { item =>
          val liftedNewItem = lift(newItem)
          item.goodId == liftedNewItem.goodId && item.orderId.exists(liftedNewItem.orderId.contains)
        })
        _ <- if (currentItems.nonEmpty) {
          val currentItem = currentItems.head
          run(schema.items.update(_.quantity -> lift(currentItem.quantity + newItem.quantity)))
        } else run(schema.items.insert(lift(newItem)))
      } yield Completed
    }
  }
}

