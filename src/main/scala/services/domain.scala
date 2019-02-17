package services

import akka.http.scaladsl.unmarshalling.Unmarshaller
import database.{Good, Item}
import io.getquill.Embedded
import services.Domain.Id

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

case class FullGoodPack(quantity: Int, good: Good) extends Embedded

case class FullOrder(userId: Id, id: Id, packs: List[FullGoodPack])

case class NewItem(userId: Id, item: Item)

case class GoodsPack(quantity: Int, goodId: Id) extends Embedded

case class NewOrder(userId: Id, packs: List[GoodsPack])
