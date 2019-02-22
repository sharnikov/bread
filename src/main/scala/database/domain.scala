package database

import database.OrderStatus.Status
import database.Role.Role
import io.getquill.Embedded
import services.Domain.Id

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

case class Order(id: Option[Id], userId: Id, status: Status)

case class Good(id: Id, name: String, category: String, price: Double) extends Embedded

case class User(id: Id,
                login: String,
                name: Option[String],
                secondName: Option[String],
                password: String,
                role: Role
               )