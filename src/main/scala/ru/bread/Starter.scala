package ru.bread

import java.io.File

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import ru.bread.modules._
import ru.bread.services.internal.RoutesBuilderImpl
import ru.bread.settings.config.{AppSettings, Settings}
import ru.bread.settings.schedulers.{MainContext, SimpleScheduledTaskManager}

object Starter extends App with MainContext with LazyLogging {

  val settings: Settings = new AppSettings(ConfigFactory.parseFile(new File("src/main/resources/app.conf")))
  logger.info("Config initialized")

  val commonModule = new CommonModule(settings)
  val dbModule = new DatabaseModule(settings)
  val authorizationModule = new AuthorizationModule(dbModule, commonModule, settings)
  val ordersModule = new OrdersModule(dbModule, commonModule, authorizationModule)
  logger.info("Modules initialized")

  val schedule = new SimpleScheduledTaskManager(authorizationModule.sessions, commonModule.timeProvider, settings)
  schedule.start()
  logger.info("Scheduled manager initialized")

  val routesBuilder = new RoutesBuilderImpl(Seq(authorizationModule, ordersModule), commonModule)
  logger.info("Routes initialized")

  val routesModule = new RoutesModule(commonModule, routesBuilder.routes(), settings)
  logger.info("App started")
}