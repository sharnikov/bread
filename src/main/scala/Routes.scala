import akka.http.scaladsl.marshalling.{ToEntityMarshaller, ToResponseMarshallable, ToResponseMarshaller}
import akka.http.scaladsl.server.Directives._
import services.{CatalogService, ServiceException}
import domain.JsonParsers._

import scala.concurrent.Future
import scala.util.{Failure, Success}

class Routes(catalogService: CatalogService) {

  def getRoutes() =
    get {
      path("all_goods") {
        completeResult(catalogService.getAllGoods())
      }
    } ~ get {
      path("goods_by_category") {
        parameters('category.as[String]) { category =>
          completeResult(catalogService.getGoodsByCategory(category))
        }
      }
    }

  private def completeResult[T](result: Future[T])(implicit marshaller: ToResponseMarshaller[T]) =
    onComplete(result) {
      case Success(info) => complete(info)
      case Failure(exception) => failWith(new ServiceException("Internal exception", exception))
    }
}
