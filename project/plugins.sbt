scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-language:postfixOps"
)

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.6.0")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "4.0.0-RC2")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"
