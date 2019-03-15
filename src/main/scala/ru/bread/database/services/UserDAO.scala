package ru.bread.database.services

import ru.bread.database.User
import ru.bread.database.settings.DatabaseSettings.PgSchema
import ru.bread.settings.schedulers.DatabaseContext

import scala.concurrent.Future

trait UserDAO {
  def getUser(login: String): Future[Option[User]]
}

class UserDAOImpl(schema: PgSchema) extends UserDAO with DatabaseContext {
  import schema._
  import schema.dbContext._

  override def getUser(login: String): Future[Option[User]] = {
      run(users.filter(_.login.toLowerCase == lift(login.toLowerCase()))).map {
        _.headOption
      }
  }
}
