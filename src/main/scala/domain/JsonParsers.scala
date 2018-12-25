package domain

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object JsonParsers extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val goodResponse: RootJsonFormat[Good] = jsonFormat2(Good)
}
