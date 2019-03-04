package ru.bread.modules

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ru.bread.database.Item
import ru.bread.database.OrderStatus.Status
import ru.bread.database.services.OrdersDAOImpl
import ru.bread.services.Domain.Id
import ru.bread.services._
import ru.bread.services.external.{OrdersService, OrdersServiceImpl}
import ru.bread.http.response.JsonParsers._
import ru.bread.http.routes.RoutesUtils

class OrdersModule(dbModule: DatabaseModule,
                   commonModule: CommonModule,
                   authorizationModule: AuthorizationModule)
  extends ModuleWithRoutes with RoutesUtils {

  override def name(): String = "Orders"

  val dao = new OrdersDAOImpl(dbModule.dbSchema)
  val catalogService = new OrdersServiceImpl(dao, commonModule.timeProvider)

  override def routes(): Route = routes(catalogService)

  def routes(catalogService: OrdersService): Route =
    get {
      path("all_goods") {
        completeResult(catalogService.getAllGoods())
      } ~ path("goods_by_category") {
        parameters('category.as[String]) { category =>
          completeResult(catalogService.getGoodsByCategory(category))
        }
      } ~ path("order_by_id") {
        parameters('userId.as[Id], 'orderId.as[Id]) { (userId, orderId) =>
          completeResult(catalogService.getOrderById(userId, orderId))
        }
      }
    }~ post {
      headerValueByName("sessionid") { headerValue =>

        val user = authorizationModule.authorizationService.userBySessionId(headerValue)
        logger.info(s"User ${user.id} was successfully authenticated")

        path("add_order") {
          entity(as[Seq[GoodsPack]]) { goodsPack =>
            completeResult(catalogService.addOrder(user.id, goodsPack))
          }
        } ~ path("change_status") {
          parameters('orderId.as[Id], 'status.as[Status]) { (orderId, status) =>
            completeResult(catalogService.changeStatus(orderId, status))
          }
        } ~ path("add_item") {
          entity(as[Item]) { newItem =>
            completeResult(catalogService.addItemToOrder(user.id, newItem))
          }
        }  ~ path("remove_item") {
          entity(as[Item]) { newItem =>
            completeResult(catalogService.removeItemFromOrder(user.id, newItem))
          }
        }
      }
    }
}
