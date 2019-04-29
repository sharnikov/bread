package ru.bread.modules

import ru.bread.database.services.OrdersDAO
import ru.bread.services.external.{OrdersService, OrdersServiceImpl}
import ru.bread.services.internal.TimeProvider

class OrdersModule(ordersDao: OrdersDAO,
                   timeProvider: TimeProvider) {

  val ordersService: OrdersService = new OrdersServiceImpl(ordersDao, timeProvider)

}
