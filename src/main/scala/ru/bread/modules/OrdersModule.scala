package ru.bread.modules

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ru.bread.database.{Item, Role}
import ru.bread.database.OrderStatus.Status
import ru.bread.database.services.OrdersDAOImpl
import ru.bread.services.Domain.Id
import ru.bread.services._
import ru.bread.services.external.{AuthorizationService, OrdersService, OrdersServiceImpl}
import ru.bread.http.response.JsonParsers._
import ru.bread.http.routes.{RoutesAuthUtils, RoutesUtils}

class OrdersModule(dbModule: DatabaseModule,
                   commonModule: CommonModule,
                   authorizationModule: AuthorizationModule)
  extends ModuleWithRoutes with RoutesUtils with RoutesAuthUtils {

  override def name(): String = "Orders"

  val dao = new OrdersDAOImpl(dbModule.dbSchema)
  val catalogService = new OrdersServiceImpl(dao, commonModule.timeProvider)

  override def authService(): AuthorizationService = authorizationModule.authorizationService

  override def routes(): Route =
    get {
      path("all_goods") {
        completeResult(catalogService.getAllGoods())
      } ~
      path("goods_by_category") {
        parameters('category.as[String]) { category =>
          completeResult(catalogService.getGoodsByCategory(category))
        }
      }
    } ~ post {
      authWithToken { user =>
        authorize(user.role == Role.CLIENT) {
          path("add_order") {
            entity(as[Seq[GoodsPack]]) { goodsPack =>
              completeResult(catalogService.addOrder(user.id, goodsPack))
            }
          } ~
          path("add_item") {
            entity(as[Item]) { newItem =>
              completeResult(catalogService.addItemToOrder(user.id, newItem))
            }
          } ~
          path("remove_item") {
            entity(as[Item]) { newItem =>
              completeResult(catalogService.removeItemFromOrder(user.id, newItem))
            }
          }
        } ~
        authorize(user.role == Role.CLIENT || user.role == Role.ADMIN) {
          path("change_status") {
              parameters('orderId.as[Id], 'status.as[Status]) { (orderId, status) =>
                completeResult(catalogService.changeStatus(orderId, status))
              }
            } ~
          path("order_by_id") {
              parameters('orderId.as[Id]) { orderId =>
                completeResult(catalogService.getOrderById(user.id, orderId))
              }
            }
          }
      }
    }
}
