name := "bread"

version := "0.1"

scalaVersion := "2.12.8"

scalacOptions += "-language:higherKinds"

resolvers ++= Seq(
  "Nexus repo" at "http://nexus.tcsbank.ru/content/repositories/tcs",
  "TCB repo" at "https://nexus-new.tcsbank.ru/repository/mvn-tcb-releases/"
)

libraryDependencies += "com.typesafe" % "config" % "1.3.2"
libraryDependencies += "com.iheart" %% "ficus" % "1.4.0"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.1.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.4",
  "com.typesafe.akka" %% "akka-http-xml" % "10.1.4",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.4",
  "com.typesafe.akka" %% "akka-stream" % "2.5.12",
  "de.heikoseeberger" %% "akka-http-circe" % "1.19.0-M3"
)

libraryDependencies += "io.spray" %% "spray-json" % "1.3.4"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.11"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.6"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.6"

libraryDependencies += "org.postgresql" % "postgresql" % "9.4.1208"
libraryDependencies += "io.getquill" %% "quill-core" % "2.6.0"
libraryDependencies += "io.getquill" %% "quill-async-postgres" % "2.6.0"
libraryDependencies += "io.getquill" %% "quill" % "2.6.0"

