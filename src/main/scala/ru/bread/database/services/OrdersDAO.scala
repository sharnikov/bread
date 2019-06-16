package ru.bread.database.services

import com.typesafe.scalalogging.LazyLogging
import ru.bread.database.OrderStatus.Status
import ru.bread.database._
import ru.bread.database.schema.PostgresSchemaAssessor
import ru.bread.errors.AppError.DatabaseDataNotFoundException
import ru.bread.http.response.Completed
import ru.bread.services.Domain.Id
import ru.bread.services.{FullGoodPack, OrderItems}
import ru.bread.settings.schedulers.DatabaseContext

import scala.concurrent.Future

trait OrdersDAO {
  def getAllGoods(): Future[List[Good]]
  def getGoodsByCategory(category: String): Future[List[Good]]
  def getOrderById(userId: Id, orderId: Id): Future[OrderItems]
  def addOrder(order: Order, items: Seq[Item]): Future[Option[Id]]
  def changeStatus(orderId: Id, status: Status): Future[Completed]
  def addItemToOrder(userId: Id, item: Item): Future[Completed]
  def removeItemFromOrder(userId: Id, removeItem: Item): Future[Completed]
}

class OrdersDAOImpl(db: PostgresSchemaAssessor) extends OrdersDAO with DatabaseContext with LazyLogging {

  import db.getAsyncSchema._
  import db.getAsyncSchema.dbContext._

  override def getAllGoods(): Future[List[Good]] = {
    run(goods)
  }

  override def getGoodsByCategory(category: String): Future[List[Good]] = {
    run(goods.filter(_.category == lift(category)))
  }

  override def getOrderById(userId: Id, orderId: Id): Future[OrderItems] =
    for {
      orders <- run(orders.filter(order => order.userId == lift(userId) && order.id.contains(lift(orderId))))
      order = orders.headOption.getOrElse(
        throw new DatabaseDataNotFoundException(s"There is no order with such an orderId = $orderId for this user")
      )
      goodsWithQuantity <- run(
        items.filter(item =>  item.orderId.contains(lift(orderId)))
          .join(goods).on((item, good) => item.goodId == good.id)
          .map { case (item, good) => FullGoodPack(item.quantity, good) }
      )
    } yield {
      OrderItems(
        packs = goodsWithQuantity,
        creationDate = order.creationDate,
        status = order.status
      )
    }

  override def addOrder(order: Order, orderItems: Seq[Item]): Future[Option[Id]] = {

    def addIdToItems(orderId: Option[Id]) = orderItems.map(_.copy(orderId = orderId))

    transaction { _ =>
      for {
        orderId <- run(orders.insert(lift(order)).returning(_.id))
        _ <- run(liftQuery(addIdToItems(orderId)).foreach(item => items.insert(item)))
      } yield orderId
    }
  }

  override def changeStatus(orderId: Id, status: Status): Future[Completed] =
    transaction { _ =>
      run(orders.filter(_.id.contains(lift(orderId))).update(_.status -> lift(status))).map(_ => Completed)
    }

  override def addItemToOrder(userId: Id, newItem: Item): Future[Completed] =
    transaction { _ =>
      for {
        orders <- run(orders.filter(order =>
          lift(newItem).orderId.exists(order.id.contains) && lift(userId) == order.userId)
        )
        order = orders.headOption
        _ = if (order.isEmpty) throw new DatabaseDataNotFoundException(s"There is no such an order")
        _ = if (order.exists(_.status != OrderStatus.NEW))
          throw new DatabaseDataNotFoundException(s"Order status is ${orders.headOption.map(_.status)}, but must be NEW")
        currentItems <- run(items.filter { item =>
          val liftedNewItem = lift(newItem)
          item.goodId == liftedNewItem.goodId && item.orderId.exists(liftedNewItem.orderId.contains)
        })
        _ <- if (currentItems.nonEmpty) {
          val currentItem = currentItems.head
          run(items.filter{ item =>
            val liftedNewItem = lift(newItem)
            item.goodId == liftedNewItem.goodId && item.orderId.exists(liftedNewItem.orderId.contains)
          }.update(_.quantity -> lift(currentItem.quantity + newItem.quantity)))
        } else run(items.insert(lift(newItem)))
      } yield Completed
    }

  override def removeItemFromOrder(userId: Id, removeItem: Item): Future[Completed] =
    transaction { _ =>
      for {
        currentItems <- run(items.filter { item =>
          val liftedNewItem = lift(removeItem)
          item.goodId == liftedNewItem.goodId && item.orderId.exists(liftedNewItem.orderId.contains)
        })
        _ = currentItems.foreach { currentItem =>
          if (currentItem.quantity - removeItem.quantity <= 0 )
            run(items.filter { item =>
              val liftedNewItem = lift(removeItem)
              item.goodId == liftedNewItem.goodId && item.orderId.exists(liftedNewItem.orderId.contains)
            }.delete) else
            run(items.filter { item =>
              val liftedNewItem = lift(removeItem)
              item.goodId == liftedNewItem.goodId && item.orderId.exists(liftedNewItem.orderId.contains)
            }.update(_.quantity -> lift(currentItem.quantity - removeItem.quantity)))
        }
        _ = if (currentItems.isEmpty) logger.warn(
          s"There are no items with id = ${removeItem.goodId} for orderId = ${removeItem.orderId} and user = $userId"
        )

      } yield Completed
    }
}