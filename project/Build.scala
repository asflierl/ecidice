import sbt._
import Keys._
import Project.Setting
import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseKeys
import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseCreateSrc

object EcidiceBuild extends Build {
  lazy val root = Project(
    id = "ecidice",
    base = file("."),
    settings = Project.defaultSettings ++ buildSettings)
    
  def buildSettings: Seq[Setting[_]] = Seq(
    name := "ecidice",
    version := "1.0",
    organization := "eu.flierl",
    
    scalaVersion := "2.10.0",
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-optimise", "-language:_"),
    autoCompilerPlugins := true,
    addCompilerPlugin("org.scala-lang.plugins" % "continuations" % "2.10.0"),
    
    fork in run := true,
    mainClass in (Compile, run) := Some("ecidice.Main"),
    
    EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource,
    
    testOptions := Seq(
      Tests.Filter(_ == "ecidice.EcidiceSpec"), 
      Tests.Argument("html", "console")),

    testOptions <+= crossTarget map { ct =>
      Tests.Setup { () => 
        System.setProperty("specs2.outDir", (ct / "specs2") absolutePath)
      }
    },

    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % "7.0-SNAPSHOT",
      "joda-time" % "joda-time" % "2.1",
      "org.joda" % "joda-convert" % "1.2",
      "com.typesafe.akka" %% "akka-actor" % "2.1.0",
      
      "org.specs2" %% "specs2" % "1.13" % "test",
      "org.scalacheck" %% "scalacheck" % "1.10.0" % "test",
      "junit" % "junit" % "4.7" % "test",
      "org.pegdown" % "pegdown" % "1.0.2" % "test",
      "org.hamcrest" % "hamcrest-all" % "1.1" % "test",
      "org.mockito" % "mockito-all" % "1.9.0" % "test"),

    unmanagedJars in Compile <<= unmanagedBase map { libs =>
      ((libs / "jme" * "jMonkeyEngine3.jar") +++ (libs / "jme" / "lib" ** "*.jar")).classpath
    },
      
    fetchJME <<= (unmanagedBase, streams, sbtVersion) map { (libs, out, version) =>
      val jme = url("http://jmonkeyengine.com:80/nightly/jME3_2012-12-24.zip")
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
      "Sonatype Snapshots"    at "http://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype Releases"     at "http://oss.sonatype.org/content/repositories/releases")
  )
  
  val fetchJME = TaskKey[Set[File]]("fetch-jme")
}
