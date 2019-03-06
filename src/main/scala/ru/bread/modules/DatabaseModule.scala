package ru.bread.modules

import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import io.getquill.{Escape, PostgresDialect}
import ru.bread.database.settings.{DatabaseSettings, PostgresSchemaImpl}
import ru.bread.settings.config.Settings

class DatabaseModule(settings: Settings) extends Module {
  override def name(): String = "Database"

  private val dbSettings = new DatabaseSettings()
  val dbSchema: PostgresSchemaImpl[PostgresDialect.type, Escape.type, PostgreSQLConnection] = new PostgresSchemaImpl(dbSettings.pgContext)
}
