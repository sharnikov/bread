import akka.http.scaladsl.server.Directives._
import services.{CatalogService, ServiceException}
import domain.JsonParsers._

import scala.util.{Failure, Success}

class Routes(catalogService: CatalogService) {

  def getRoutes() =
    get {
      path("all_goods") {
        onComplete(catalogService.getAllGoods()) {
          case Success(info) => complete(info)
          case Failure(exception) => failWith(new ServiceException("Internal exception", exception))
        }
      }
    } ~ get {
      path("goods_by_category") {
        parameters('category.as[String]) { category =>
          onComplete(catalogService.getGoodsByCategory(category)) {
            case Success(info) => complete(info)
            case Failure(exception) => failWith(new ServiceException("Internal exception", exception))
          }
        }
      }
    }
}
