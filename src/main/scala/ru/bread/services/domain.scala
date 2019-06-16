package ru.bread.services

import java.util.Date

import io.getquill.Embedded
import ru.bread.database.Good
import ru.bread.database.OrderStatus.Status
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
                     status: String,
                     totalPrice: Double)

case class OrderItems(packs: Seq[FullGoodPack],
                      creationDate: Date,
                      status: Status)

case class GoodsPack(quantity: Int, goodId: Id) extends Embedded

case class LogAndPass(login: String, password: String)