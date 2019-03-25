import sbt.librarymanagement._

object Dependencies extends DependencyBuilders with LibraryManagementSyntax {

  private val akkaVersion = "10.1.7"
  private val jacksonVersion = "2.9.6"
  private val quillVersion = "3.1.0"
  private val akkaStreamsVersion = "2.5.19"

  val logs = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
  )

  val typesafeConfig = Seq(
    "com.typesafe" % "config" % "1.3.3",
    "com.iheart" %% "ficus" % "1.4.0"
  )

  val utilLibraries = Seq(
    "org.typelevel" %% "cats-core" % "1.1.0",
    "commons-codec" % "commons-codec" % "1.12"
  )

  val akka = Seq(
    "com.typesafe.akka" %% "akka-http-caching" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-xml" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaVersion,
    "de.heikoseeberger" %% "akka-http-circe" % "1.19.0-M3" exclude("com.typesafe.akka", "akka-actor"),
    "com.typesafe.akka" %% "akka-stream" % akkaStreamsVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaStreamsVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaStreamsVersion % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion % Test
  )

  val json = Seq(
    "io.spray" %% "spray-json" % "1.3.5",
    "org.json4s" %% "json4s-native" % "3.2.11",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
  )

  val database = Seq(
    "org.postgresql" % "postgresql" % "9.4.1208",
    "io.getquill" %% "quill-core" % quillVersion,
    "io.getquill" %% "quill-async-postgres" % quillVersion,
    "io.getquill" %% "quill" % quillVersion,
  )

  val testing = Seq(
    "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    "org.scalamock" %% "scalamock" % "4.1.0" % Test
  )

  val all = typesafeConfig ++ utilLibraries ++ testing ++ database ++ json ++ akka ++ logs
}
