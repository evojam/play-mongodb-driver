scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.11.7")

name := "play-mongodb-driver"

organization := "com.evojam"

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ywarn-adapted-args",
  "-Ywarn-value-discard",
  "-Ywarn-inaccessible",
  "-Ywarn-dead-code"
)

licenses +=("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

resolvers ++= Seq(
  Resolver.defaultLocal,
  Resolver.sbtPluginRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeRepo("snapshots"),
  Resolver.typesafeRepo("releases"),
  Resolver.typesafeIvyRepo("releases")
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "2.4.11" % "test",
  "org.specs2" %% "specs2-junit" % "2.4.11" % "test",
  "com.typesafe.play" % "play_2.11" % "2.4.2",
  "com.evojam" % "mongo-driver-scala_2.11" % "0.5.0"
)
