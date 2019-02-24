import java.util.Date
import java.util.concurrent.{ConcurrentHashMap, ScheduledExecutorService}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import database._
import services.{CatalogServiceImpl, SimpleAuthorizationService}
import settings.{MainContext, SimpleScheduledTaskManager}
import settings.config.{AppSettings, Settings}

object Starter extends App with MainContext with LazyLogging {

  val settings: Settings = new AppSettings(ConfigFactory.load("app"))
  logger.info("Config initialized")
  implicit val system: ActorSystem = ActorSystem("bread")
  logger.info("ActorSystem initialized")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  logger.info("ActorMaterializer initialized")

  val sessions: ConcurrentHashMap[String, Date] = new ConcurrentHashMap[String, Date]()
  val schedule = new SimpleScheduledTaskManager(settings, sessions)
  schedule.start()

  val dbSettings = new DatabaseSettings()
  val dbSchema = new PostgresSchema(dbSettings.pgContext)
  val dao = new OrdersDAOImpl(dbSchema)
  val userDao = new UserDAOImpl(dbSchema)
  logger.info("Database services initialized")

  val catalogService = new CatalogServiceImpl(dao)
  val authorizationService = new SimpleAuthorizationService(userDao, sessions)
  logger.info("Services initialized")

  val routes = new Routes(catalogService, authorizationService, sessions)
  logger.info("Routes initialized")

  Http().bindAndHandle(routes.getRoutes(), settings.akkaSettings().host, settings.akkaSettings().port)
  logger.info("App started")
}
