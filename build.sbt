organization := "com.example"

name := "collabdraw"

version := "0.1.0"

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-filter" % "0.5.2",
  "net.databinder" %% "unfiltered-jetty" % "0.5.2",
  "net.databinder" %% "unfiltered-netty-websockets" % "0.5.2",
  "com.mongodb.casbah" %% "casbah"  % "2.1.5-1"
)

resolvers += "scala-tools-repo" at "http://scala-tools.org/repo-releases/"

scalacOptions := Seq("-deprecation")

seq(lsSettings :_*)
