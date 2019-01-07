package domain

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

object JsonParsers extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val goodResponse = jsonFormat4(Good)

  implicit val fullGoodpack = jsonFormat2(FullGoodPack)

  implicit val fullOrder = jsonFormat3(FullOrder)

  implicit val goodsPack = jsonFormat2(GoodsPack)

  implicit val newOrder = jsonFormat2(NewOrder)
}

