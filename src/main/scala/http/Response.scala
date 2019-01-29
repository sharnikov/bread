package http

import java.util.Date

import errors.AppError
import spray.json.{JsObject, JsString, JsValue, JsonFormat, RootJsonWriter}
import http.TimeJsonProtocol._

trait Response[+A]

object Response {

  implicit def jsonSuccessParser[T : JsonFormat]= new RootJsonWriter[SuccessfulResponse[T]] {

      override def write(obj: SuccessfulResponse[T]): JsValue = {
        JsObject(
          "payload" -> implicitly[JsonFormat[T]].write(obj.payload),
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

  case class SuccessfulResponse[T](payload: T, time: Date) extends Response[T]
  case class FailedResponse(error: AppError, time: Date) extends Response[Nothing]

  def success[T](payload: T) = SuccessfulResponse(payload, new Date())
  def fail(error: AppError) = FailedResponse(error, new Date())
}