package ru.bread.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import ru.bread.modules.Module
import ru.bread.services.security.SSLContextProducer
import ru.bread.settings.config.Settings

class RoutesModule(sslContextProducer: SSLContextProducer, routes: Route, settings: Settings) extends Module {
  override def name(): String = "Routes"

  implicit val system: ActorSystem = ActorSystem("bread")
  logger.info("ActorSystem initialized")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  logger.info("ActorMaterializer initialized")

  Http().bindAndHandle(
    routes,
    settings.akkaHttpSettings().host,
    settings.akkaHttpSettings().port,
    connectionContext = sslContextProducer.getConnectionContext()
  )
}
