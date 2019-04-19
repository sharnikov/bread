package ru.bread.database

import java.util.Date

import ru.bread.database.OrderStatus.Status
import ru.bread.database.Role.Role
import io.getquill.Embedded
import ru.bread.database.settings.DbEnum
import ru.bread.services.Domain.Id

object OrderStatus extends DbEnum {

  type Status = Value

  val NEW, DONE, WAITING_FOR_CONFIRM, IN_PROGRESS, REJECTED, UNKNOWN = Value

  override def default(): OrderStatus.Value = UNKNOWN
}

object Role extends DbEnum {

  type Role = Value

  val GHOST = Value(0)
  val CLIENT = Value(1)
  val TRUSTED_CLIENT = Value(2)
  val ADMIN = Value(3)
  val GOD = Value(4)

  override def default(): Role.Value = GHOST
}

case class Item(goodId: Id, orderId: Option[Id], quantity: Int)

case class Order(id: Option[Id], userId: Id, status: Status, creationDate: Date)

case class Good(id: Id, name: String, category: String, price: Double) extends Embedded

case class User(id: Id,
                login: String,
                password: String,
                mail: String,
                name: Option[String],
                secondName: Option[String],
                role: Role
               )

