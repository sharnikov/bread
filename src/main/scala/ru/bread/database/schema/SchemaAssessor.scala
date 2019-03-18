package ru.bread.database.schema

import com.github.mauricio.async.db.Connection
import io.getquill.NamingStrategy
import io.getquill.context.sql.idiom.SqlIdiom
import ru.bread.database.settings.DatabaseSettings.{PgAsyncSchema, PgSchema}

trait SchemaAssessor {
  val getSchema: Schema[_ <: SqlIdiom, _ <: NamingStrategy]
  val getAsyncSchema: AsyncSchema[_ <: SqlIdiom, _ <: NamingStrategy, _ <: Connection]
}

class PostgresSchemaAssessor(postgresSchema: PgAsyncSchema) extends SchemaAssessor {
  override val getSchema: PgSchema = postgresSchema
  override val getAsyncSchema: PgAsyncSchema = postgresSchema
}