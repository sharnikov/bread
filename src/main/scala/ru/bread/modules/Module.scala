package ru.bread.modules

import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging

trait Module extends LazyLogging {
  def name(): String
}

trait ModuleWithRoutes extends Module {
  def routes(): Route
}
