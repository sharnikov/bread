package http

import java.text.SimpleDateFormat
import java.util.Date

import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import errors.AppError
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import errors.AppError.ServiceException
import spray.json.{DefaultJsonProtocol, JsArray, JsNumber, JsObject, JsString, JsValue, JsonFormat, RootJsonFormat}
import http.TimeJsonProtocol._

trait Response[+A]

object Response {

  implicit def jsonSuccessParser[T : JsonFormat]: RootJsonFormat[SuccessfulResponse[T]] =
    new RootJsonFormat[SuccessfulResponse[T]] {

      override def read(json: JsValue): SuccessfulResponse[T] = json match {
        case JsObject(fields) => parseResponse(fields)
      }

      override def write(obj: SuccessfulResponse[T]): JsValue = {
        JsObject(
          "payload" -> implicitly[JsonFormat[T]].write(obj.payload),
          "time" -> dateFormatSerializator.write(obj.time)
        )
      }

      private def parseResponse(fields: Map[String, JsValue]): SuccessfulResponse[T] =
        (for {
          payload <- fields.get("payload")
          time <- fields.get("time")
        } yield {
          SuccessfulResponse(
            payload = payload.convertTo[T],
            time = time.convertTo[Date]
          )
        }).getOrElse(throw new ServiceException(s"Can't parse SuccessfulResponse from the fields $fields"))
    }

  implicit def jsonFailParser = new RootJsonFormat[FailedResponse] {
    override def read(json: JsValue): FailedResponse = ???

    override def write(obj: FailedResponse): JsValue = ???
  }

  case class SuccessfulResponse[T](payload: T, time: Date) extends Response[T]
  case class FailedResponse(error: AppError, time: Date) extends Response[Nothing]

  def success[T](payload: T) = SuccessfulResponse(payload, new Date())
  def fail(error: AppError) = FailedResponse(error, new Date())
}
