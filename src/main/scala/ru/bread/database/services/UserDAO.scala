package ru.bread.database.services

import ru.bread.database.{RegistrationUser, User}
import ru.bread.database.schema.PostgresSchemaAssessor
import ru.bread.http.response.Completed
import ru.bread.settings.schedulers.DatabaseContext

import scala.concurrent.Future

trait UserDAO {
  def getUser(login: String): Future[Option[User]]
  def getUsersByLoginOrMail(login: String, mail: String): Future[Seq[User]]
  def registerNewUser(user: RegistrationUser): Future[Completed]
}

class UserDAOImpl(db: PostgresSchemaAssessor) extends UserDAO with DatabaseContext {
  import db.getAsyncSchema._
  import db.getAsyncSchema.dbContext._

  override def getUser(login: String): Future[Option[User]] = {
      run(users.filter(_.login.toLowerCase == lift(login.toLowerCase()))).map {
        _.headOption
      }
  }

  override def getUsersByLoginOrMail(login: String, mail: String): Future[Seq[User]] = {
    run(users.filter(
      user => user.login.toLowerCase == lift(login.toLowerCase) && user.mail.toLowerCase == lift(mail.toLowerCase)
    ))
  }

  override def registerNewUser(user: RegistrationUser): Future[Completed] = {
    run(newUsers.insert(lift(user))).map(_ => Completed)
  }
}
