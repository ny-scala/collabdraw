

resolvers ++= Seq(
  "less is" at "http://repo.lessis.me",
  "sbt-idea-repo" at "http://mpeltonen.github.com/maven/",
  "coda" at "http://repo.codahale.com")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "0.11.0")

addSbtPlugin("me.lessis" % "ls-sbt" % "0.1.0")
