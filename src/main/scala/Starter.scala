import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import modules._
import settings.{MainContext, SimpleScheduledTaskManager}
import settings.config.{AppSettings, Settings}

object Starter extends App with MainContext with LazyLogging {

  val settings: Settings = new AppSettings(ConfigFactory.load("app"))
  logger.info("Config initialized")

  val dbModule = new DatabaseModule(settings)
  val ordersModule = new OrdersModule(dbModule)
  val authorizationModule = new AuthorizationModule(dbModule)
  val catalogModule = new CatalogModule(ordersModule.dao)

  val schedule = new SimpleScheduledTaskManager(settings, authorizationModule.sessions)
  schedule.start()

  val routes = new Routes(
    catalogModule.catalogService,
    authorizationModule.authorizationService,
    authorizationModule.sessions
  )
  val routesModule = new RoutesModule(settings, routes.getRoutes())
  logger.info("App started")
}
