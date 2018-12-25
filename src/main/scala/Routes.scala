import akka.http.scaladsl.server.Directives._
import services.CatalogService
import domain.JsonParsers._

import scala.util.Success

class Routes(catalogService: CatalogService) {

  def getRoutes() =
    get {
      path("get_all_goods") {
        onComplete(catalogService.getAllGoods()) {
          case Success(info) => complete(info)
        }
      }
    }
}
