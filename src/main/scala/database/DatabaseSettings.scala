package database

import com.github.mauricio.async.db.Configuration
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.postgresql.pool.PostgreSQLConnectionFactory
import com.github.mauricio.async.db.postgresql.util.URLParser
import com.typesafe.config.{Config, ConfigFactory}
import io.getquill.context.async.AsyncContextConfig
import io.getquill.{Escape, PostgresAsyncContext}
import settings.DatabaseContext

import scala.concurrent.ExecutionContext

class DatabaseSettings extends DatabaseContext {

  type DbContext = PostgresAsyncContext[Escape]

  private val postgresConfig = PostgresConfig(ConfigFactory.load("db"), context)

  case class PostgresConfig(config: Config, executionContext: ExecutionContext)
    extends AsyncContextConfig[PostgreSQLConnection](
      config = config,
      connectionFactory = (configuration: Configuration) => new PostgreSQLConnectionFactory(
        configuration = configuration,
        executionContext = executionContext
      ),
      uriParser = URLParser
    )

  val pgContext: DbContext = new PostgresAsyncContext(Escape, postgresConfig.pool)
}
