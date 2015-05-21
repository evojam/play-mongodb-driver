scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.11.6")

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
  Resolver.url(
    "Artifactory Realm",
    new URL("http://artifacts-repository-1.hetzner:8081/artifactory/libs-snapshot-local"))(Resolver.ivyStylePatterns),
  Resolver.mavenLocal,
  Resolver.sbtPluginRepo("snapshots"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.typesafeRepo("snapshots"),
  Resolver.typesafeRepo("releases"),
  Resolver.typesafeIvyRepo("releases")
)

libraryDependencies ++= Seq(
  "com.typesafe.play" % "play_2.11" % "2.4.0-RC5",
  "com.evojam" % "mongo-driver-scala_2.11" % "0.1.0-SNAPSHOT"
)