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
      libraryDependencies ++= Dependencies.all
    )

