package ru.bread.database.services

import ru.bread.database.User
import ru.bread.database.schema.PostgresSchemaAssessor
import ru.bread.settings.schedulers.DatabaseContext

import scala.concurrent.Future

trait UserDAO {
  def getUser(login: String): Future[Option[User]]
}

class UserDAOImpl(db: PostgresSchemaAssessor) extends UserDAO with DatabaseContext {
  import db.getAsyncSchema._
  import db.getAsyncSchema.dbContext._

  override def getUser(login: String): Future[Option[User]] = {
      run(users.filter(_.login.toLowerCase == lift(login.toLowerCase()))).map {
        _.headOption
      }
  }
}
