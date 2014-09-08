name := """btdt"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "commons-beanutils" % "commons-beanutils" % "1.9.2",
   "org.mindrot" % "jbcrypt" % "0.3m"
  )