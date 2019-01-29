package http

import java.text.SimpleDateFormat
import java.util.Date

import errors.AppError.ServiceException
import spray.json.{JsString, JsValue, RootJsonFormat}

import scala.util.Try

trait TimeJsonProtocol {

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

  implicit val dateFormatSerializator = new RootJsonFormat[Date] {
    def write(date: Date) = JsString(dateToIsoString(date))
    def read(json: JsValue) = json match {
      case JsString(rawDate) =>
        parseIsoDateString(rawDate).getOrElse(throw new ServiceException(s"Expected ISO Date format, got $rawDate"))
      case error => throw new ServiceException(s"Expected JsString, got $error")
    }
  }

  private def dateToIsoString(date: Date): String = dateFormat.format(date)
  private def parseIsoDateString(date: String): Option[Date] = Try{ dateFormat.parse(date) }.toOption
}

object TimeJsonProtocol extends TimeJsonProtocol
