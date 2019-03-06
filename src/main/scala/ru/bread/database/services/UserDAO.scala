package ru.bread.database.services

import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import io.getquill.{Escape, PostgresDialect}
import ru.bread.database.User
import ru.bread.database.settings.PostgresSchemaImpl
import ru.bread.settings.schedulers.DatabaseContext

import scala.concurrent.Future

trait UserDAO {
  def getUser(login: String): Future[Option[User]]
}

class UserDAOImpl(schema: PostgresSchemaImpl[PostgresDialect.type, Escape.type, PostgreSQLConnection]) extends UserDAO with DatabaseContext {
  import schema._
  import schema.dbContext._

  override def getUser(login: String): Future[Option[User]] = {
      run(users.filter(_.login.toLowerCase == lift(login.toLowerCase()))).map {
        _.headOption
      }
  }
}
