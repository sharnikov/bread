package domain

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object JsonParsers extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val goodResponse: RootJsonFormat[Good] = jsonFormat4(Good)

  implicit val goodsPack: RootJsonFormat[GoodsPack] = jsonFormat2(GoodsPack)
  implicit val fullOrder: RootJsonFormat[FullOrder] = jsonFormat3(FullOrder)
}
