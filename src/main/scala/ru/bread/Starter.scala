package ru.bread

import java.io.File

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import ru.bread.modules._
import ru.bread.services.internal.RoutesBuilderImpl
import ru.bread.settings.config.{AppSettings, Settings, UpdatableAppSettings}
import ru.bread.settings.schedulers.MainContext

object Starter extends App with MainContext with LazyLogging {

  val settings: Settings = new AppSettings(ConfigFactory.parseFile(new File("src/main/resources/app.conf")))
  val updatableSettings = new UpdatableAppSettings(() => settings)
  logger.info("Config initialized")

  val commonModule = new CommonModule(updatableSettings)
  val dbModule = new DatabaseModule(updatableSettings)
  val authorizationModule = new AuthorizationModule(dbModule, commonModule, updatableSettings)
  val ordersModule = new OrdersModule(dbModule, commonModule, authorizationModule)
  val schedulerModule = new SchedulerModule(authorizationModule.sessions, commonModule.timeProvider, updatableSettings)
  logger.info("Modules initialized")

  val routesBuilder = new RoutesBuilderImpl(Seq(authorizationModule, ordersModule), commonModule)
  logger.info("Routes initialized")

  val routesModule = new RoutesModule(commonModule, routesBuilder.routes(), updatableSettings)
  logger.info("App started")
}