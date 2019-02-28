package ru.bread.modules

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ru.bread.database.OrderStatus.Status
import ru.bread.database.services.OrdersDAOImpl
import ru.bread.services.Domain.Id
import ru.bread.services._
import ru.bread.services.external.{OrdersService, OrdersServiceImpl}
import ru.bread.http.response.JsonParsers._
import ru.bread.http.routes.RoutesUtils

class OrdersModule(dbModule: DatabaseModule,
                   commonModule: CommonModule,
                   sessions: ConcurrentHashMap[String, Date])
  extends ModuleWithRoutes with RoutesUtils {

  override def name(): String = "Orders"

  val dao = new OrdersDAOImpl(dbModule.dbSchema)
  val catalogService = new OrdersServiceImpl(dao, commonModule.timeProvider)

  override def routes(): Route = routes(catalogService)

  def routes(catalogService: OrdersService): Route =
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
      path("add_order") {
        entity(as[NewOrder]) { order =>
          completeResult(catalogService.addOrder(order))
        }
      }
    } ~ post {
      path("change_status") {
        parameters('orderId.as[Id], 'status.as[Status]) { (orderId, status) =>
          completeResult(catalogService.changeStatus(orderId, status))
        }
      }
    } ~ post {
      path("add_item") {
        entity(as[NewItem]) { newItem =>
          completeResult(catalogService.addItemToOrder(newItem))
        }
      }
    }
}
