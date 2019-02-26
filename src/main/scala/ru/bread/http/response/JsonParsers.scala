package ru.bread.http.response

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import ru.bread.database.{Good, Item}
import ru.bread.services._
import spray.json.DefaultJsonProtocol

object JsonParsers extends DefaultJsonProtocol with SprayJsonSupport with TimeJsonParser {

  implicit val idResponseFormat = jsonFormat1(ResponseWithId)

  implicit val sessionIdFormat = jsonFormat1(SessionId)

  implicit val goodResponseFormat = jsonFormat4(Good)

  implicit val fullGoodpackFormat = jsonFormat2(FullGoodPack)

  implicit val fullOrderFormat = jsonFormat4(FullOrder)

  implicit val goodsPackFormat = jsonFormat2(GoodsPack)

  implicit val newOrderFormat = jsonFormat2(NewOrder)

  implicit val itemFormat = jsonFormat3(Item)

  implicit val newItemFormat = jsonFormat2(NewItem)

  implicit val logAndPassFormat = jsonFormat2(LogAndPass)
}