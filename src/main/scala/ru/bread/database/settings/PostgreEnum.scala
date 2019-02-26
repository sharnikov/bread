package ru.bread.database.settings

import akka.http.scaladsl.unmarshalling.Unmarshaller

trait PostgreEnum extends Enumeration {

  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)

  def withNameWithDefault(name: String): Value =
    values.find(_.toString.toLowerCase == name.toLowerCase()).getOrElse(default())

  implicit val stringToMyType = {
    Unmarshaller.strict[String, Value](withNameWithDefault)
  }

  def default(): Value
}
