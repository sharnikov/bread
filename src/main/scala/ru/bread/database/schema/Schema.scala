package ru.bread.database.schema

import com.github.mauricio.async.db.Connection
import io.getquill.NamingStrategy
import io.getquill.context.Context
import io.getquill.context.async.AsyncContext
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.idiom.Idiom
import ru.bread.database._

trait Schema[D <: Idiom, N <: NamingStrategy] {

  val dbContext: Context[D, N]

  import dbContext._

  val goods = quote {
    querySchema[Good]("goods",
      _.id -> "id",
      _.name -> "name",
      _.price -> "price",
      _.category -> "category"
    )
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
      _.mail -> "mail",
      _.name -> "name",
      _.secondName -> "secondname",
      _.password -> "password",
      _.role -> "role"
    )
  }

  val newUsers = quote {
    querySchema[RegistrationUser]("users",
      _.login -> "login",
      _.mail -> "mail",
      _.name -> "name",
      _.secondName -> "secondname",
      _.password -> "password"
    )
  }
}

trait AsyncSchema[D <: SqlIdiom, N <: NamingStrategy, C <: Connection] extends Schema[D, N] {
  override val dbContext: AsyncContext[D, N, C]
}