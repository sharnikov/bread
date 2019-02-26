package http

import java.util.Date

import errors.AppError
import errors.AppError.ParseFailedException
import spray.json.{JsObject, JsString, JsValue, JsonFormat, JsonWriter, RootJsonReader, RootJsonWriter}
import http.TimeJsonProtocol._

trait Response[+A]

object Response {

  implicit def jsonSuccessReader[T](implicit payloadFormat : JsonFormat[T])= new RootJsonReader[SuccessfulResponse[T]] {
    override def read(json: JsValue): SuccessfulResponse[T] = json match {
      case JsObject(fields) => pasreSuccesfulResponse(fields)
      case _ => throw new ParseFailedException(s"Can't parse SuccessfulResponse in $json")
    }

    private def pasreSuccesfulResponse(fields: Map[String, JsValue]) =
      (for {
        payload <- fields.get("payload")
        time <- fields.get("time")
      } yield {
        SuccessfulResponse(
          payload = payloadFormat.read(payload),
          time = implicitly[JsonFormat[Date]]read(time)
        )
      }).getOrElse(throw new ParseFailedException(s"Can't parse SuccessfulResponse with fields = $fields"))
  }

  implicit def jsonSuccessWriter[T](implicit payloadFormat : JsonWriter[T])= new RootJsonWriter[SuccessfulResponse[T]] {

    override def write(obj: SuccessfulResponse[T]): JsValue = {
      JsObject(
        "payload" -> implicitly[JsonWriter[T]].write(obj.payload),
        "time" -> dateFormatSerializator.write(obj.time)
      )
    }
  }

  implicit def jsonFailParser = new RootJsonWriter[FailedResponse] {

    override def write(obj: FailedResponse): JsValue = {
      JsObject(
        "errorMessage" -> JsString(obj.error.message),
        "errorCode" -> JsString(obj.error.code.toString),
        "time" -> dateFormatSerializator.write(obj.time)
      )
    }
  }

  case class  SuccessfulResponse[T](payload: T, time: Date) extends Response[T]
  case class FailedResponse(error: AppError, time: Date) extends Response[Nothing]

  def success[T](payload: T) = SuccessfulResponse(payload, new Date())
  def fail(error: AppError) = FailedResponse(error, new Date())
}