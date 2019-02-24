package modules

import database.OrdersDAO
import services.CatalogServiceImpl

class CatalogModule(ordersDao: OrdersDAO) extends Module {
  override def name(): String = "Catalog"

  val catalogService = new CatalogServiceImpl(ordersDao)
}
