package modules

import database.OrdersDAOImpl

class OrdersModule(dbModule: DatabaseModule) extends Module {
  override def name(): String = "Orders"

  val dao = new OrdersDAOImpl(dbModule.dbSchema)
}
