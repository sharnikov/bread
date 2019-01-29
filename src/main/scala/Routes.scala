import java.util.Date

import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Directives._
import services.CatalogService
import domain.OrderStatus.Status
import domain.JsonParsers._
import domain.NewOrder
import domain.Domain._
import errors.AppError.{BreadException, ServiceException}
import errors.ErrorCode
import spray.json.JsonFormat

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
    } ~ post {
      path ("add_order") {
        entity(as[NewOrder]) { order =>
          completeResult(catalogService.addOrder(order))
        }
      }
    } ~ get {
      path ("change_status") {
        parameters('orderId.as[Id], 'status.as[Status]) { (orderId, status) =>
          onComplete(catalogService.changeStatus(orderId, status)) {
            case Success(_) => complete("OK")
            case Failure(exception) => failWith(
              new ServiceException("Can't update the status", exception)
            )
          }
        }
      }
    }

  private def completeResult[T : JsonFormat](result: Future[T]) = {
    import http.Response._

    onComplete(result) {
      case Success(info) => complete(success(info))
      case Failure(exception) => failWith(
        new BreadException(ErrorCode.InternalError, "Internal exception", exception)
      )
    }
  }

}
