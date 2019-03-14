package ru.bread.modules

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import ru.bread.settings.config.Settings

class RoutesModule(commonModule: CommonModule, routes: Route, settings: Settings) extends Module {
  override def name(): String = "Routes"

  implicit val system: ActorSystem = ActorSystem("bread")
  logger.info("ActorSystem initialized")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  logger.info("ActorMaterializer initialized")

  Http().bindAndHandle(
    routes,
    settings.akkaSettings().host,
    settings.akkaSettings().port,
    connectionContext = commonModule.SSLContextProducer.getConnectionContext()
  )
}
