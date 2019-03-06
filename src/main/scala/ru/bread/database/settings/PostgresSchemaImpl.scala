package ru.bread.database.settings

import com.github.mauricio.async.db.Connection
import io.getquill.context.async.{AsyncContext, SqlTypes}
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.NamingStrategy
import org.postgresql.util.PGobject
import ru.bread.database.OrderStatus.Status
import ru.bread.database.Role.Role
import ru.bread.database._

class PostgresSchemaImpl[D <: SqlIdiom, E <: NamingStrategy, C <: Connection](override val dbContext: AsyncContext[D, E, C]) extends Schema[D, E] {

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

}