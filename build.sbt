name := "processes"
version := "1.0"
scalaVersion := "2.11.8"

val logbackVersion = "1.1.7"

resolvers += "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "ch.qos.logback" % "logback-core" % logbackVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)
