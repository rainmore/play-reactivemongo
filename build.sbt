name := """backend"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

//offline := true

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test,
  "org.reactivemongo" %% "reactivemongo" % "0.11.5",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.5.play24"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
