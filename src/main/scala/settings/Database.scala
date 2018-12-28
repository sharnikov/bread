package settings

import domain.{Good, Item, Order}
import io.getquill.{Escape, PostgresAsyncContext}
import settings.config.Settings

object DatabaseUtils {
  type DbContext = PostgresAsyncContext[Escape]
}

class Database(settings: Settings) {

  val context = new PostgresAsyncContext[Escape](Escape, settings.databaseSettings().databaseConfigName())

  import context._

  val goods = quote {
    querySchema[Good]("goods")
  }

  val items = quote {
    querySchema[Item]("items", _.quantity -> "quantity", _.goodId -> "good_id", _.orderId -> "order_id")
  }

  val orders = quote {
    querySchema[Order]("orders", _.id -> "id", _.userId -> "user_id")
  }
}
