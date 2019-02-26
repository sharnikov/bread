package ru.bread.http

import java.io.Serializable

import spray.json.{JsString, JsValue, RootJsonReader, RootJsonWriter}

sealed abstract class Completed extends Serializable
case object Completed extends Completed {

  implicit val completedWriter = new RootJsonWriter[Completed] {
    def write(date: Completed) = JsString("Done")
  }

  implicit val completedReader = new RootJsonReader[Completed] {
    override def read(json: JsValue): Completed = json match {
      case JsString("Done") => Completed
    }
  }
}
