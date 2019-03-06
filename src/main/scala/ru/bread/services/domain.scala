package ru.bread.services

import java.util.Date

import ru.bread.database.{Good, Item}
import io.getquill.Embedded
import ru.bread.services.Domain.Id

object Domain {
  type Id = Int
}

case class ResponseWithId(id: Id)

case class SessionId(id: String)

case class FullGoodPack(quantity: Int, good: Good) extends Embedded

case class FullOrder(userId: Id,
                     id: Id,
                     packs: Seq[FullGoodPack],
                     creationDate: Date,
                     totalPrice: Double)

case class OrderItems(packs: Seq[FullGoodPack],
                      creationDate: Date)

case class GoodsPack(quantity: Int, goodId: Id) extends Embedded

case class LogAndPass(login: String, password: String)