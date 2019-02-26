package ru.bread.services.internal

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ru.bread.http.routes.RoutesSettings
import ru.bread.modules.ModuleWithRoutes

trait RoutesBuilder {
  def routes(): Route
}

class RoutesBuilderImpl(modules: Seq[ModuleWithRoutes]) extends RoutesBuilder with RoutesSettings {
  override def routes(): Route = Route.seal(
    modules.map(_.routes()).reduceLeft(_ ~ _)
  )
}
