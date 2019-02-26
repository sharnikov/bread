lazy val settings = Seq(
  name := "bread",
  organization := "Pet project Inc",
  scalaVersion := "2.12.8",
  version := "0.1"
)

lazy val root = (project in file("."))
    .settings(
      settings,
      scalacOptions := Seq("-language:higherKinds"),
      resolvers := Seq(
        Resolver.url("Snapshots", url("http://nexus.tcsbank.ru/content/repositories/snapshots")),
        Resolver.url("TCS Plugin Snapshots", url("http://nexus.tcsbank.ru/content/repositories/tcs")),
        Resolver.url("SBT Plugin Releases", url("https://dl.bintray.com/sbt/sbt-plugin-releases"))
      ),
      libraryDependencies ++= Dependencies.all
    )

