package ru.bread.database.schema

import com.github.mauricio.async.db.Connection
import io.getquill.NamingStrategy
import io.getquill.context.async.{AsyncContext, SqlTypes}
import io.getquill.context.sql.idiom.SqlIdiom
import org.postgresql.util.PGobject
import ru.bread.database.OrderStatus.Status
import ru.bread.database.Role.Role
import ru.bread.database._
import ru.bread.database.settings.DbEnum

class PostgresSchema[D <: SqlIdiom, N <: NamingStrategy, C <: Connection](override val dbContext: AsyncContext[D, N, C]) extends AsyncSchema[D, N, C] {

  import dbContext._

  private def makeDecoder[T <: DbEnum](enum: T): Decoder[enum.Value] = decoder(
    {
      case value: String =>  enum.withNameWithDefault(value)
    },
    SqlTypes.VARCHAR
  )

  private def makeEncoder[T <: DbEnum](enum: T): Encoder[enum.Value] = encoder(
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
}