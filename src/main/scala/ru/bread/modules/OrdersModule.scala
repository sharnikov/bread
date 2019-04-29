package ru.bread.modules

import ru.bread.database.services.OrdersDAO
import ru.bread.services.external.{OrdersService, OrdersServiceImpl}
import ru.bread.services.internal.TimeProvider

class OrdersModule(ordersDao: OrdersDAO,
                   timeProvider: TimeProvider) extends Module {

  override def name(): String = "Orders module"

  val ordersService: OrdersService = new OrdersServiceImpl(ordersDao, timeProvider)

}
