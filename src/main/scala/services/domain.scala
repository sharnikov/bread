package services

import java.util.Date

import database.{Good, Item}
import io.getquill.Embedded
import services.Domain.Id

object Domain {
  type Id = Int
}

case class ResponseWithId(id: Id)

case class SessionId(id: String)

case class FullGoodPack(quantity: Int, good: Good) extends Embedded

case class FullOrder(userId: Id, id: Id, packs: List[FullGoodPack], creationDate: Date)

case class NewItem(userId: Id, item: Item)

case class GoodsPack(quantity: Int, goodId: Id) extends Embedded

case class NewOrder(userId: Id, packs: List[GoodsPack])

case class LogAndPass(login: String, password: String)