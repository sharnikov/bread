package settings

import domain.{Good, Item, Order}
import io.getquill.{Escape, PostgresAsyncContext}

object DatabaseUtils {
  type DbContext = PostgresAsyncContext[Escape]
}

class Database {

  val context = new PostgresAsyncContext[Escape](Escape, "bread.db")

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
