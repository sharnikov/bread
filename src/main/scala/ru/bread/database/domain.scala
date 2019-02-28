package ru.bread.database

import java.util.Date

import ru.bread.database.OrderStatus.Status
import ru.bread.database.Role.Role
import io.getquill.Embedded
import ru.bread.database.settings.PostgreEnum
import ru.bread.services.Domain.Id

object OrderStatus extends PostgreEnum {

  type Status = Value

  val NEW, DONE, IN_PROGRESS, REJECTED, UNKNOWN = Value

  override def default(): OrderStatus.Value = UNKNOWN
}

object Role extends PostgreEnum {

  type Role = Value

  val ADMIN, CLIENT, GOD, GHOST, TRUSTED_CLIENT = Value

  override def default(): Role.Value = GHOST
}

case class Item(goodId: Id, orderId: Option[Id], quantity: Int)

case class Order(id: Option[Id], userId: Id, status: Status, creationDate: Date)

case class Good(id: Id, name: String, category: String, price: Double) extends Embedded

case class User(id: Id,
                login: String,
                password: String,
                name: Option[String],
                secondName: Option[String],
                role: Role
               )

