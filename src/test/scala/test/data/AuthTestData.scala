package test.data

import ru.bread.database.{Role, User}
import test.data.CommonTestData._
import ru.bread.services.{LogAndPass, SessionId}

import scala.util.Random

object AuthTestData {

  val login = "Vova"
  val password = "228"
  val encryptedPassword = "veryEncrypted228"
  val name = "Name"
  val secondName = "SecondName"

  val sessionId = Random.alphanumeric.take(30).mkString
  val wrappedSessionId = SessionId(sessionId)

  val user = User(
    id = userId,
    login = login,
    name = Some(name),
    secondName = Some(secondName),
    password = None,
    Role.CLIENT
  )

  val logAndPass = LogAndPass(login, password)

}
