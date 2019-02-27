package ru.bread

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import ru.bread.modules._
import ru.bread.services.internal.RoutesBuilderImpl
import ru.bread.settings.config.{AppSettings, Settings}
import ru.bread.settings.schedulers.{MainContext, SimpleScheduledTaskManager}

object Starter extends App with MainContext with LazyLogging {

  val settings: Settings = new AppSettings(ConfigFactory.load("app.conf"))
  logger.info("Config initialized")

  val commonModule = new CommonModule(settings)
  val dbModule = new DatabaseModule(settings)
  val authorizationModule = new AuthorizationModule(dbModule, commonModule, settings)
  val ordersModule = new OrdersModule(dbModule, commonModule, authorizationModule.sessions)

  val schedule = new SimpleScheduledTaskManager(authorizationModule.sessions, settings)
  schedule.start()

  val routesBuilder = new RoutesBuilderImpl(Seq(authorizationModule, ordersModule))

  val routesModule = new RoutesModule(settings, routesBuilder.routes())
  logger.info("App started")
}
