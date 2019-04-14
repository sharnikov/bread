package ru.bread.services.internal

import akka.http.scaladsl.model.DateTime
import akka.http.scaladsl.model.headers.Date
import akka.http.scaladsl.model.headers.`Access-Control-Allow-Origin`
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ru.bread.http.routes.RoutesSettings
import ru.bread.modules.{CommonModule, ModuleWithRoutes}

trait RoutesBuilder {
  def routes(): Route
}

class RoutesBuilderImpl(modules: Seq[ModuleWithRoutes], commonModule: CommonModule) extends RoutesBuilder
  with RoutesSettings {

  override def routes(): Route = Route.seal(
    mapResponseHeaders(_ :+ Date(DateTime(commonModule.timeProvider.currentTime.getTime)) :+ `Access-Control-Allow-Origin`.*
    ) {
      modules.map(_.routes()).reduceLeft(_ ~ _)
    }
  )
}
