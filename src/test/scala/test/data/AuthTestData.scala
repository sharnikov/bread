package test.data

import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.server.directives.Credentials.Provided
import test.data.CommonTestData._
import ru.bread.services.{LogAndPass, SessionId}
import ru.bread.database.{Role, User}

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
    password = encryptedPassword,
    name = Some(name),
    secondName = Some(secondName),
    Role.CLIENT
  )

  val logAndPass = LogAndPass(login, password)

  val basicHttpCredentials = BasicHttpCredentials(login, password)

  val trueCredentials = new Provided(login) {
    override def verify(secret: String, hasher: String => String): Boolean = true
  }

  val falseCredentials = new Provided(login) {
    override def verify(secret: String, hasher: String => String): Boolean = false
  }

}
