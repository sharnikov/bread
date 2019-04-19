package ru.bread.services.internal

import akka.http.scaladsl.model.{DateTime, HttpMethods, HttpResponse, StatusCodes}
import akka.http.scaladsl.model.headers._
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
    mapResponseHeaders(_ :+ Date(DateTime(commonModule.timeProvider.currentTime.getTime))) {
      modules.map(_.routes()).reduceLeft(_ ~ _)
    } ~ options { context =>
      val resp = HttpResponse(StatusCodes.OK, headers = List(
        `Access-Control-Allow-Origin`(HttpOriginRange.*),
        `Access-Control-Allow-Methods`(HttpMethods.GET, HttpMethods.POST, HttpMethods.OPTIONS),
        `Access-Control-Allow-Headers`("content-type" +: "authorization" +: "version" +: context.request.headers.map(_.lowercaseName): _*)
      ))
      context.complete(resp)
    }
  )
}
