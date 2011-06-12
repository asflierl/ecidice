name := "ecidice"

version := "1.0"

organization := "eu.flierl"

scalaVersion := "2.9.0-1"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-optimise", "-explaintypes")

autoCompilerPlugins := true

addCompilerPlugin("org.scala-lang.plugins" % "continuations" % "2.9.0-1")

fork in run := true

mainClass in (Compile, run) := Some("ecidice.Main")

testOptions := Seq(
  Tests.Filter(_ == "ecidice.EcidiceSpec"), 
  Tests.Argument("html", "console"))

testOptions <+= crossTarget map { ct =>
  Tests.Setup { () => System.setProperty("specs2.outDir", new File(ct, "specs2").getAbsolutePath) }
}

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.5-SNAPSHOT" % "test",
  "joda-time" % "joda-time" % "1.6.2",
  "org.scalaz" %% "scalaz-core" % "6.0",
  "se.scalablesolutions.akka" % "akka" % "1.1.2"
)

unmanagedJars in Compile <<= baseDirectory map { base => (base ** "*.jar").classpath }

TaskKey[Set[File]]("fetch-jme") <<= (unmanagedBase, streams) map { (libs, out) =>
  val jme = url("http://jmonkeyengine.com/nightly/jME3_2011-06-12.zip")
  val target = libs / "jme"
  IO.delete(target)
  IO.createDirectory(target)
  out.log.info("Fetching jME3 from " + jme)
  IO.unzipURL(jme, target, AllPassFilter)
} 

resolvers ++= Seq(
  "snapshots" at "http://scala-tools.org/repo-snapshots",
  "releases" at "http://scala-tools.org/repo-releases",
  "Akka Repo" at "http://akka.io/repository"
)
