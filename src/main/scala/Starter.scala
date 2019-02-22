import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import database.{DatabaseSettings, OrdersDAOImpl, PostgresSchema}
import services.CatalogServiceImpl
import settings.MainContext
import settings.config.{AppSettings, Settings}

object Starter extends App with MainContext with LazyLogging {

  val settings: Settings = new AppSettings(ConfigFactory.load("app"))
  logger.info("Config initialized")
  implicit val system: ActorSystem = ActorSystem("bread")
  logger.info("ActorSystem initialized")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  logger.info("ActorMaterializer initialized")

  val dbSettings = new DatabaseSettings()
  val dbSchema = new PostgresSchema(dbSettings.pgContext)
  val dao = new OrdersDAOImpl(dbSchema)

  val catalogService = new CatalogServiceImpl(dao)
  logger.info("Services initialized")

  val routes = new Routes(catalogService)
  logger.info("Routes initialized")

  Http().bindAndHandle(routes.getRoutes(), settings.akkaSettings().host, settings.akkaSettings().port)
  logger.info("App started")
}
