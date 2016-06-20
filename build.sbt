name := "playsimplenewsfeed"

version := "1.0"

lazy val `playsimplenewsfeed` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( javaJdbc , jdbc, cache , javaWs)

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.36"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"  