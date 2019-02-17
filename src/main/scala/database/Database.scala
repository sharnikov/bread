package database

import com.github.mauricio.async.db.Configuration
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.postgresql.pool.PostgreSQLConnectionFactory
import com.github.mauricio.async.db.postgresql.util.URLParser
import com.typesafe.config.{Config, ConfigFactory}
import domain.OrderStatus.Status
import domain.OrderStatus
import io.getquill.context.async.{AsyncContextConfig, SqlTypes}
import io.getquill.{Escape, PostgresAsyncContext}
import org.postgresql.util.PGobject
import settings.DBContext

import scala.concurrent.ExecutionContext

object Database extends DBContext {

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

  val pgContext: PostgresAsyncContext[Escape] = new PostgresAsyncContext(Escape, postgresConfig.pool)

  import pgContext._

  implicit val statusDecoder: Decoder[Status] =
    decoder(
      {
        case value =>  OrderStatus.withNameWithDefault(value.toString)
      },
      SqlTypes.VARCHAR
    )

  implicit val statusEncoder: Encoder[Status] =
    encoder(
      value => {
        val pgObject = new PGobject()
        pgObject.setType("TEXT")
        pgObject.setValue(value.toString)
        pgObject
      },
      SqlTypes.VARCHAR
    )

  val goods = quote {
    querySchema[Good]("goods")
  }

  val items = quote {
    querySchema[Item]("items", _.quantity -> "quantity", _.goodId -> "good_id", _.orderId -> "order_id")
  }

  val orders = quote {
    querySchema[Order]("orders", _.id -> "id", _.userId -> "user_id", _.status -> "status")
  }

}
