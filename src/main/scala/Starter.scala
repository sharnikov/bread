import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import database.{DAOImpl, DatabaseSettings, PostgresSchema}
import services.CatalogServiceImpl
import settings.MainContext
import settings.config.{AppSettings, Settings}

object Starter extends App with MainContext {

  val settings: Settings = new AppSettings(ConfigFactory.load("app"))

  implicit val system: ActorSystem = ActorSystem("bread")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val dbSettings = new DatabaseSettings()
  val dbSchema = new PostgresSchema(dbSettings.pgContext)
  val dao = new DAOImpl(dbSchema)

  val catalogService = new CatalogServiceImpl(dao)

  val routes = new Routes(catalogService)

  Http().bindAndHandle(routes.getRoutes(), settings.akkaSettings().host, settings.akkaSettings().port)

}
