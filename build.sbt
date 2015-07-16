name := "work-assigner"

version := "1.0"

lazy val `work-assigner` = (project in file(".")).enablePlugins(PlayScala)

resolvers := ("Atlassian Releases" at "https://maven.atlassian.com/public/") +: resolvers.value

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , cache , ws,
  "net.codingwell" %% "scala-guice" % "4.0.0",
  "javax.inject" % "javax.inject" % "1",
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
  "com.typesafe.play" %% "anorm" % "2.4.0",
  "com.mohiva" %% "play-silhouette" % "2.0",
//  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.4.0",
  // web jars
  "org.webjars" % "bootstrap" % "3.3.1",
  "org.webjars" % "angularjs" % "1.4.1",
  "org.webjars" % "angular-ui-bootstrap" % "0.12.0",
  "org.webjars" % "angular-strap" % "2.2.3",
  "org.webjars" % "angular-ui-router" % "0.2.15",
  "org.webjars" % "satellizer" % "0.11.1",
  // test
  "org.mockito" % "mockito-core" % "1.10.17" % "test",
  "com.mohiva" %% "play-silhouette-testkit" % "2.0" % "test"
  )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  