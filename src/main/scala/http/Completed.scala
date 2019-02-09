package http

import java.io.Serializable

import spray.json.{JsString, RootJsonWriter}

sealed abstract class Completed extends Serializable
case object Completed extends Completed {

  implicit val completedWriter = new RootJsonWriter[Completed] {
    def write(date: Completed) = JsString("Done")
  }
}
