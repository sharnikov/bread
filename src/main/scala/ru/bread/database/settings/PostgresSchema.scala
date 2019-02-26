package ru.bread.database.settings

import io.getquill.context.async.SqlTypes
import io.getquill.{Escape, PostgresAsyncContext}
import org.postgresql.util.PGobject
import ru.bread.database.OrderStatus.Status
import ru.bread.database.Role.Role
import ru.bread.database._

class PostgresSchema(val dbContext: PostgresAsyncContext[Escape]) {

  import dbContext._

  private def makeDecoder[T <: PostgreEnum](enum: T): Decoder[enum.Value] = decoder(
    {
      case value =>  enum.withNameWithDefault(value.toString)
    },
    SqlTypes.VARCHAR
  )

  private def makeEncoder[T <: PostgreEnum](enum: T): Encoder[enum.Value] = encoder(
    value => {
      val pgObject = new PGobject()
      pgObject.setType("TEXT")
      pgObject.setValue(value.toString)
      pgObject
    },
    SqlTypes.VARCHAR
  )

  implicit val statusDecoder: Decoder[Status] = makeDecoder(OrderStatus)
  implicit val statusEncoder: Encoder[Status] = makeEncoder(OrderStatus)

  implicit val roleDecoder: Decoder[Role] = makeDecoder(Role)
  implicit val roleEncoder: Encoder[Role] = makeEncoder(Role)


  val goods = quote {
    querySchema[Good]("goods")
  }

  val items = quote {
    querySchema[Item]("items", _.quantity -> "quantity", _.goodId -> "good_id", _.orderId -> "order_id")
  }

  val orders = quote {
    querySchema[Order]("orders", _.id -> "id", _.userId -> "user_id", _.status -> "status", _.creationDate -> "creation_date")
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