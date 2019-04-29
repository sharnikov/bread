package ru.bread

import java.io.File

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import ru.bread.modules._
import ru.bread.routes.{AuthorizationRoutes, OrdersRoutes, RoutesModule}
import ru.bread.services.internal.RoutesBuilderImpl
import ru.bread.settings.config.{AppSettings, Settings, UpdatableAppSettings}
import ru.bread.settings.schedulers.MainContext

object Starter extends App with MainContext with LazyLogging {

  val settings: Settings = new AppSettings(ConfigFactory.parseFile(new File("src/main/resources/app.conf")))
  val updatableSettings = new UpdatableAppSettings(() => settings)
  logger.info("Config initialized")

  val dbModule = new DatabaseModule(updatableSettings)
  val commonModule = new CommonModule(dbModule.userDao, updatableSettings)
  val authorizationModule = new AuthorizationModule(
    dbModule.userDao,
    commonModule.sessionGenerator,
    commonModule.encryptService,
    commonModule.timeProvider
  )

  val authorizationRoutes = new AuthorizationRoutes(
    authorizationModule.authorizationService,
    commonModule.validationService,
    updatableSettings
  )

  val ordersModule = new OrdersModule(dbModule.ordersDao, commonModule.timeProvider)

  val ordersRoutes = new OrdersRoutes(
    ordersModule.ordersService,
    commonModule.cacheService,
    authorizationModule.authorizationService
  )

  val schedulerModule = new SchedulerModule(authorizationModule.sessions, commonModule.timeProvider, updatableSettings)
  logger.info("Modules initialized")

  val routesBuilder = new RoutesBuilderImpl(Seq(authorizationRoutes, ordersRoutes), commonModule.timeProvider)
  logger.info("Routes initialized")

  val routesModule = new RoutesModule(commonModule.sslContextProducer, routesBuilder.routes(), updatableSettings)
  logger.info("App started")
}