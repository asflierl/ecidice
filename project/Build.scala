import sbt._
import Keys._
import Project.Setting
import JMonkeyProject._

object EcidiceBuild extends Build {
  lazy val root = Project(
    id = "ecidice",
    base = file("."),
    settings = Project.defaultSettings ++ buildSettings ++ jmonkeySettings)
    
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

    jmonkey.targetVersion := "2011-02-14",
    
    resolvers ++= Seq(
      "Typesafe Repository"   at "http://repo.typesafe.com/typesafe/releases/",
      "sonatype snapshots"    at "http://oss.sonatype.org/content/repositories/snapshots",
      "sonatype releases"     at "http://oss.sonatype.org/content/repositories/releases")
  )
}