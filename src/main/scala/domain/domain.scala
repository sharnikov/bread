package domain

import akka.http.scaladsl.unmarshalling.Unmarshaller
import domain.Domain.Id
import domain.OrderStatus.Status
import io.getquill.Embedded

object Domain {
  type Id = Int
}

object OrderStatus extends Enumeration {
  type Status = Value

  val NEW, DONE, IN_PROGRESS, REJECTED, UNKNOWN = Value

  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)

  def withNameWithDefault(name: String): Value =
    values.find(_.toString.toLowerCase == name.toLowerCase()).getOrElse(UNKNOWN)

  implicit val stringToMyType = {
    Unmarshaller.strict[String, Status](withNameWithDefault)
  }
}

case class ResponseWithId(id: Id)

case class Good(id: Id, name: String, category: String, price: Double) extends Embedded

case class Order(id: Option[Id], userId: Id, status: Status)

case class Item(goodId: Id, orderId: Option[Id], quantity: Int)

case class User(id: Id, login: String, name: Option[String], secondName: Option[String])

case class FullGoodPack(quantity: Int, good: Good) extends Embedded

case class FullOrder(userId: Id, id: Id, packs: List[FullGoodPack])

case class GoodsPack(quantity: Int, goodId: Id) extends Embedded

case class NewOrder(userId: Id, packs: List[GoodsPack])
