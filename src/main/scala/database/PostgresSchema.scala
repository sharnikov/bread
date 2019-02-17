package database

import io.getquill.{Escape, PostgresAsyncContext}
import io.getquill.context.async.SqlTypes
import org.postgresql.util.PGobject
import services.OrderStatus
import services.OrderStatus.Status

class PostgresSchema(val dbContext: PostgresAsyncContext[Escape]) {

  import dbContext._

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