package ru.bread.database.settings

import io.getquill.context.Context
import io.getquill.NamingStrategy
import io.getquill.idiom.Idiom
import ru.bread.database._

trait Schema[D <: Idiom, N <: NamingStrategy] {

  val dbContext: Context[D, N]

  import dbContext._

  val goods = quote {
    querySchema[Good]("goods")
  }

  val items = quote {
    querySchema[Item]("items",
      _.quantity -> "quantity",
      _.goodId -> "good_id",
      _.orderId -> "order_id"
    )
  }

  val orders = quote {
    querySchema[Order]("orders",
      _.id -> "id",
      _.userId -> "user_id",
      _.status -> "status",
      _.creationDate -> "creation_date"
    )
  }

  val users = quote {
    querySchema[User]("users",
      _.id -> "id",
      _.login -> "login",
      _.name -> "name",
      _.secondName -> "secondname",
      _.password -> "password",
      _.role -> "role"
    )
  }

}