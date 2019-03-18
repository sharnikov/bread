package ru.bread.modules

import ru.bread.database.schema.{PostgresSchema, PostgresSchemaAssessor}
import ru.bread.database.settings.DatabaseSettings
import ru.bread.settings.config.Settings

class DatabaseModule(settings: Settings) extends Module {
  override def name(): String = "Database"

  private val dbSettings = new DatabaseSettings()

  private val dbSchema = new PostgresSchema(dbSettings.pgContext)

  val schemaAssessor: PostgresSchemaAssessor = new PostgresSchemaAssessor(dbSchema)
}