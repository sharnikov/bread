package ru.bread.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ru.bread.database.OrderStatus.Status
import ru.bread.database.{Item, Role}
import ru.bread.http.response.JsonParsers._
import ru.bread.http.routes.{RoutesAuthUtils, RoutesUtils}
import ru.bread.modules.ModuleWithRoutes
import ru.bread.services.Domain.Id
import ru.bread.services._
import ru.bread.services.caching.CacheService
import ru.bread.services.external.{AuthorizationService, OrdersService}


class OrdersRoutes(ordersService: OrdersService,
                   cacheService: CacheService,
                   val authorizationService: AuthorizationService) extends ModuleWithRoutes with RoutesUtils
  with RoutesAuthUtils {

  override def name(): String = "Orders routes module"

  import cacheService._

  override def routes(): Route =
    get {
      cacheRouteByUrl {
        path("all_goods") {
          completeResult(ordersService.getAllGoods())
        } ~
        path("goods_by_category") {
          parameters('category.as[String]) { category =>
            completeResult(ordersService.getGoodsByCategory(category))
          }
        }
      }
    } ~
    post {
      pathPrefix("order") {
        authWithToken { user =>
          invalidateByUser(user) {
            authorize(user.role == Role.CLIENT) {
              path("add_order") {
                entity(as[Seq[GoodsPack]]) { goodsPack =>
                  completeResult(ordersService.addOrder(user.id, goodsPack))
                }
              } ~
              path("add_item") {
                entity(as[Item]) { newItem =>
                  completeResult(ordersService.addItemToOrder(user.id, newItem))
                }
              } ~
              path("remove_item") {
                entity(as[Item]) { newItem =>
                  completeResult(ordersService.removeItemFromOrder(user.id, newItem))
                }
              }
            } ~
            authorize(user.role == Role.CLIENT || user.role == Role.ADMIN) {
              path("change_status") {
                  parameters('orderId.as[Id], 'status.as[Status]) { (orderId, status) =>
                    completeResult(ordersService.changeStatus(orderId, status))
                  }
                }
            }
          } ~
          cacheRouteByUser(user) {
              path("order_by_id") {
                parameters('orderId.as[Id]) { orderId =>
                  completeResult(ordersService.getOrderById(user.id, orderId))
                }
              }
          }
        }
      }
    }
}
