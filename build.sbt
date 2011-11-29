organization := "com.example"

name := "collabdraw"

version := "0.1.0"

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-filter" % "0.5.2",
  "net.databinder" %% "unfiltered-jetty" % "0.5.2",
  "net.databinder" %% "unfiltered-netty-websockets" % "0.5.2"
)

scalacOptions := Seq("-deprecation")

seq(lsSettings :_*)
