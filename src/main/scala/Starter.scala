import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import services.CatalogServiceImpl
import settings.Executors
import settings.config.{AppSettings, Settings}

object Starter extends App with Executors {

  val settings: Settings = new AppSettings(ConfigFactory.load())

  implicit val system: ActorSystem = ActorSystem("bread")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext = mainServiceContext()

  val catalogService = new CatalogServiceImpl()

  val routes = new Routes(catalogService)

  Http().bindAndHandle(routes.getRoutes(), settings.akkaSettings().host, settings.akkaSettings().port)

}
