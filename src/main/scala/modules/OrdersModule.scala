package modules

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import database.OrderStatus.Status
import database.OrdersDAOImpl
import http.Routes
import services.Domain.Id
import services.{OrdersService, OrdersServiceImpl, NewItem, NewOrder}
import settings.JsonParsers._
import settings.schedulers.MainContext

class OrdersModule(dbModule: DatabaseModule, sessions: ConcurrentHashMap[String, Date])
  extends Module with Routes with MainContext {

  override def name(): String = "Orders"

  val dao = new OrdersDAOImpl(dbModule.dbSchema)
  val catalogService = new OrdersServiceImpl(dao)

  override def routes() = routes(catalogService)

  def routes(catalogService: OrdersService): Route = Route.seal(
    get {
      path("all_goods") {
        logger.warn(sessions.toString)
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
  )
}
