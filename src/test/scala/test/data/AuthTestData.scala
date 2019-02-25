package test.data

import database.LogAndPass
import services.SessionId

import scala.util.Random

object AuthTestData {

  val login = "Vova"
  val password = "228"
  val name = "Name"
  val secondName = "SecondName"
  val sessionId = SessionId(Random.alphanumeric.take(30).mkString)

  val logAndPass = LogAndPass(login, password)

}
