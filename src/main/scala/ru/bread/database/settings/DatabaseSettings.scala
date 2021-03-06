package ru.bread.database.settings

import java.io.File

import com.github.mauricio.async.db.Configuration
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.postgresql.pool.PostgreSQLConnectionFactory
import com.github.mauricio.async.db.postgresql.util.URLParser
import com.typesafe.config.{Config, ConfigFactory}
import io.getquill.context.async.AsyncContextConfig
import io.getquill.{Escape, PostgresAsyncContext, PostgresDialect}
import ru.bread.database.schema.{PostgresSchema, Schema}
import ru.bread.database.settings.DatabaseSettings.PostgresConfig
import ru.bread.settings.schedulers.DatabaseContext

import scala.concurrent.ExecutionContext

class DatabaseSettings extends DatabaseContext {

  private val postgresConfig = PostgresConfig(ConfigFactory.parseFile(new File("src/main/resources/db.conf")), context)

  val pgContext = new PostgresAsyncContext(Escape, postgresConfig.pool)
}

object DatabaseSettings {

  case class PostgresConfig(config: Config, executionContext: ExecutionContext)
    extends AsyncContextConfig[PostgreSQLConnection](
      config = config,
      connectionFactory = (configuration: Configuration) => new PostgreSQLConnectionFactory(
        configuration = configuration,
        executionContext = executionContext
      ),
      uriParser = URLParser
    )

  type PgSchema = Schema[PostgresDialect.type, Escape.type]
  type PgAsyncSchema = PostgresSchema[PostgresDialect.type, Escape.type, PostgreSQLConnection]
}