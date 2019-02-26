package http

import java.text.SimpleDateFormat
import java.util.Date

import errors.AppError.ParseFailedException
import spray.json.{JsString, JsValue, RootJsonFormat}

import scala.util.Try

trait TimeJsonProtocol {

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

  implicit val dateFormatSerializator = new RootJsonFormat[Date] {
    def write(date: Date): JsValue = JsString(dateToIsoString(date))
    def read(json: JsValue): Date = json match {
      case JsString(rawDate) => parseIsoDateString(rawDate).getOrElse(
        throw new ParseFailedException(s"Expected ISO Date format, got $rawDate")
      )
      case error => throw new ParseFailedException(s"Expected JsString, got $error")
    }
  }

  private def dateToIsoString(date: Date): String = dateFormat.format(date)
  private def parseIsoDateString(date: String): Option[Date] = Try{ dateFormat.parse(date) }.toOption
}

object TimeJsonProtocol extends TimeJsonProtocol
