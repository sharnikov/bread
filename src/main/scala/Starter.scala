import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import modules._
import settings.config.{AppSettings, Settings}
import akka.http.scaladsl.server.Directives._
import settings.schedulers.{MainContext, SimpleScheduledTaskManager}

object Starter extends App with MainContext with LazyLogging {

  val settings: Settings = new AppSettings(ConfigFactory.load("app"))
  logger.info("Config initialized")

  val dbModule = new DatabaseModule(settings)
  val authorizationModule = new AuthorizationModule(dbModule, settings)
  val ordersModule = new OrdersModule(dbModule, authorizationModule.sessions)

  val schedule = new SimpleScheduledTaskManager(authorizationModule.sessions, settings)
  schedule.start()

  val routes = authorizationModule.routes() ~ ordersModule.routes()

  val routesModule = new RoutesModule(settings, routes)
  logger.info("App started")
}
