package database

import io.getquill.Embedded
import services.Domain.Id
import services.OrderStatus.Status

case class Item(goodId: Id, orderId: Option[Id], quantity: Int)

case class Order(id: Option[Id], userId: Id, status: Status)

case class Good(id: Id, name: String, category: String, price: Double) extends Embedded

case class User(id: Id, login: String, name: Option[String], secondName: Option[String])