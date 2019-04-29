package ru.bread.modules

import ru.bread.database.schema.{PostgresSchema, PostgresSchemaAssessor}
import ru.bread.database.services.{OrdersDAOImpl, UserDAO, UserDAOImpl}
import ru.bread.database.settings.DatabaseSettings
import ru.bread.settings.config.Settings

class DatabaseModule(settings: Settings) extends Module {
  override def name(): String = "Database module"

  private val dbSettings = new DatabaseSettings()

  private val dbSchema = new PostgresSchema(dbSettings.pgContext)

  val schemaAssessor: PostgresSchemaAssessor = new PostgresSchemaAssessor(dbSchema)
  val userDao: UserDAO = new UserDAOImpl(schemaAssessor)
  val ordersDao = new OrdersDAOImpl(schemaAssessor)
}