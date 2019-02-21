import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.LazyLogging
import services.{CatalogService, NewItem, NewOrder}
import settings.JsonParsers._
import http.Completed._
import errors.AppError.{ServiceException, VerboseServiceException}
import services.Domain.Id
import services.OrderStatus.Status
import spray.json.JsonWriter

import scala.concurrent.Future
import scala.util.{Failure, Success}

class Routes(catalogService: CatalogService) extends LazyLogging {

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
    } ~ post {
      path ("change_status") {
        parameters('orderId.as[Id], 'status.as[Status]) { (orderId, status) =>
          completeResult(catalogService.changeStatus(orderId, status))
        }
      }
    } ~ post {
      path ("add_item") {
        entity(as[NewItem]) { newItem =>
          completeResult(catalogService.addItemToOrder(newItem))
        }
      }
    }

  private def completeResult[T : JsonWriter](result: Future[T]) = {
    import http.Response._

    onComplete(result) {
      case Success(info) =>
        logger.info("Successful result = {}", info)
        complete(success(info))
      case Failure(exception: VerboseServiceException) =>
        logger.info("Failed with a verboseException", exception)
        complete(fail(exception))
      case Failure(exception) =>
        logger.info("Failed with an exception", exception)
        complete(fail(new ServiceException("Internal exception", exception)))
    }
  }

}
