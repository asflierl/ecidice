import sbt._
import Keys._
import Project.Setting

object EcidiceBuild extends Build {
  lazy val root = Project(
    id = "ecidice",
    base = file("."),
    settings = Project.defaultSettings ++ buildSettings)
    
  def buildSettings: Seq[Setting[_]] = Seq(
    name := "ecidice",
    version := "1.0",
    organization := "eu.flierl",
    
    scalaVersion := "2.9.1",
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-optimise", "-explaintypes"),
    autoCompilerPlugins := true,
    addCompilerPlugin("org.scala-lang.plugins" % "continuations" % "2.9.1"),
    
    fork in run := true,
    mainClass in (Compile, run) := Some("ecidice.Main"),
    
    testOptions := Seq(
      Tests.Filter(_ == "ecidice.EcidiceSpec"), 
      Tests.Argument("html", "console")),

    testOptions <+= crossTarget map { ct =>
      Tests.Setup { () => 
        System.setProperty("specs2.outDir", new File(ct, "specs2").getAbsolutePath)
      }
    },

    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % "6.0.4",
      "org.scala-tools.time" %% "time" % "0.5",
      "se.scalablesolutions.akka" % "akka" % "1.2",
      
      "org.specs2" %% "specs2" % "1.8" % "test",
      "org.scala-tools.testing" %% "scalacheck" % "1.9" % "test",
      "junit" % "junit" % "4.7" % "test",
      "org.pegdown" % "pegdown" % "1.0.2" % "test",
      "org.hamcrest" % "hamcrest-all" % "1.1" % "test",
      "org.mockito" % "mockito-all" % "1.9.0" % "test"),

    unmanagedJars in Compile <<= unmanagedBase map { libs => (libs ** "*.jar").classpath },
      
    fetchJME <<= (unmanagedBase, streams, sbtVersion) map { (libs, out, version) =>
      val jme = url("http://jmonkeyengine.com:80/nightly/jME3_2012-02-14.zip")
      val target = libs / "jme"
      IO delete target
      IO createDirectory target
      out.log.info("Fetching jME3 from " + jme)
      
      val conn = jme.openConnection
      conn.setRequestProperty("User-Agent", "sbt/" + version);
      val stream = conn.getInputStream
      
      try {
        IO.unzipStream(stream, target, AllPassFilter)
      } finally {
        stream.close
      }
    },
    
    resolvers ++= Seq(
      "Typesafe Repository"   at "http://repo.typesafe.com/typesafe/releases/",
      "sonatype snapshots"    at "http://oss.sonatype.org/content/repositories/snapshots",
      "sonatype releases"     at "http://oss.sonatype.org/content/repositories/releases")
  )
  
  val fetchJME = TaskKey[Set[File]]("fetch-jme")
}