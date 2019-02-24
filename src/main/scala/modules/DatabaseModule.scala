package modules

import database.{DatabaseSettings, PostgresSchema}
import settings.config.Settings

class DatabaseModule(settings: Settings) extends Module {
  override def name(): String = "Database"

  val dbSettings = new DatabaseSettings()
  val dbSchema = new PostgresSchema(dbSettings.pgContext)
}
