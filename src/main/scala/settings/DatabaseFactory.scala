package settings

import io.getquill.{Escape, PostgresAsyncContext}

object DatabaseFactory {
  type DbContext = PostgresAsyncContext[Escape]

  def getQuillContext() = new PostgresAsyncContext[Escape](Escape, "bread.db")
}
