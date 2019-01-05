package domain

import domain.Domain.Id
import io.getquill.Embedded

object Domain {
  type Id = Int
}

case class Good(id: Id, name: String, category: String, price: Double) extends Embedded

case class Order(id: Id, userId: Id)

case class Item(goodId: Id, orderId: Id, quantity: Int)

case class User(id: Id, login: String, name: Option[String], secondName: Option[String])

case class GoodsPack(quantity: Int, good: Good) extends Embedded

case class FullOrder(userId: Id, orderId: Option[Id], goods: List[GoodsPack])
