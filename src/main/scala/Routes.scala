import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Directives._
import domain.Domain._
import domain.JsonParsers._
import services.{CatalogService, ServiceException}

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
    } ~ get {
      path("order_by_id") {
        parameters('userId.as[Id], 'orderId.as[Id]) { (userId, orderId) =>
          completeResult(catalogService.getOrderById(userId, orderId))
        }
      }
    }

  private def completeResult[T](result: Future[T])(implicit marshaller: ToResponseMarshaller[T]) =
    onComplete(result) {
      case Success(info) => complete(info)
      case Failure(exception) => failWith(new ServiceException("Internal exception", exception))
    }
}
