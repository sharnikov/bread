package ru.bread.modules

import ru.bread.database.settings.{DatabaseSettings, PostgresSchema}
import ru.bread.settings.config.Settings

class DatabaseModule(settings: Settings) extends Module {
  override def name(): String = "Database"

  val dbSettings = new DatabaseSettings()
  val dbSchema = new PostgresSchema(dbSettings.pgContext)
}
