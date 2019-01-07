package settings

import domain.OrderStatus.Status
import domain.{Good, Item, Order, OrderStatus}
import io.getquill.context.async.SqlTypes
import io.getquill.{Escape, PostgresAsyncContext}
import org.postgresql.util.PGobject

object Database {

  type DbContext = PostgresAsyncContext[Escape]

  val context = new PostgresAsyncContext[Escape](Escape, "bread.db")

  import context._

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


