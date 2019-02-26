import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import modules._
import settings.config.{AppSettings, Settings}
import services.RoutesBuilderImpl
import settings.schedulers.{MainContext, SimpleScheduledTaskManager}

object Starter extends App with MainContext with LazyLogging {

  val settings: Settings = new AppSettings(ConfigFactory.load("app"))
  logger.info("Config initialized")

  val commonModule = new CommonModule()
  val dbModule = new DatabaseModule(settings)
  val authorizationModule = new AuthorizationModule(dbModule, settings)
  val ordersModule = new OrdersModule(dbModule, commonModule, authorizationModule.sessions)

  val schedule = new SimpleScheduledTaskManager(authorizationModule.sessions, settings)
  schedule.start()

  val routesBuilder = new RoutesBuilderImpl(Seq(authorizationModule, ordersModule))

  val routesModule = new RoutesModule(settings, routesBuilder.routes())
  logger.info("App started")
}
