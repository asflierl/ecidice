addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.3.0")

addSbtPlugin("de.johoop" % "ant4sbt" % "1.1.2")

libraryDependencies += "org.tmatesoft.svnkit" % "svnkit" % "1.7.8"

scalacOptions := Seq("-deprecation", "-unchecked", "-optimise", "-language:_", "-encoding", "UTF-8")
