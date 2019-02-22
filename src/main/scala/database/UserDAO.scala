package database

import settings.DatabaseContext

import scala.concurrent.Future

trait UserDAO {
  def getUser(login: String, password: String): Future[Option[User]]
}

class UserDAOImpl(schema: PostgresSchema) extends UserDAO with DatabaseContext {
  import schema._
  import schema.dbContext._

  override def getUser(login: String, password: String): Future[Option[User]] = {
      run(users.filter(user => user.login == login && user.password == password)).map {
        _.headOption
      }
  }
}
