import sbt._
import Keys._
import com.typesafe.sbteclipse.plugin.EclipsePlugin.{EclipseKeys, EclipseCreateSrc}
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.wc.{SVNClientManager, SVNRevision}
import org.tmatesoft.svn.core.wc.SVNRevision.{HEAD, WORKING, create => rev}
import org.tmatesoft.svn.core.SVNDepth.INFINITY
import de.johoop.ant4sbt.Ant4Sbt._
import Def.{bind, value, Setting}

object EcidiceBuild extends Build {
  lazy val root = Project(id = "ecidice", base = file("."))
    .settings(Project.defaultSettings:_*)
    .settings(antSettings:_*)
    .settings(addAntTasks("clean", "jar"):_*)
    .settings(
      name := "ecidice",
      version := "1.0",
      organization := "eu.flierl",
      
      scalaVersion := "2.10.2",
      scalacOptions := Seq("-deprecation", "-unchecked", "-optimise", "-language:_", "-encoding", "UTF-8"),
      autoCompilerPlugins := true,
      addCompilerPlugin("org.scala-lang.plugins" % "continuations" % "2.10.2"),
      
      fork in run := true,
      mainClass in (Compile, run) := Some("ecidice.Main"),
      
      EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource,
      
      testOptions := Seq(
        Tests.Filter(_ == "index"), 
        Tests.Argument("html", "console")),
  
      testOptions += {
        Tests.Setup { () => 
          sys.props += "specs2.outDir" -> (crossTarget.value / "specs2" absolutePath)
        }
      },
  
      libraryDependencies ++= Seq(
        "org.scalaz" %% "scalaz-core" % "7.0.3",
        "org.scalaz" %% "scalaz-effect" % "7.0.3",
        "com.chuusai" %% "shapeless" % "1.2.4",
        "com.github.nscala-time" %% "nscala-time" % "0.6.0",
        "org.spire-math" %% "spire" % "0.6.1",
        "com.typesafe.akka" %% "akka-actor" % "2.2.1",
        
        "org.specs2" %% "specs2" % "2.2.2" % "test",
        "org.scalacheck" %% "scalacheck" % "1.10.1" % "test",
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        "junit" % "junit" % "4.11" % "test",
        "org.pegdown" % "pegdown" % "1.2.1" % "test",
        "org.hamcrest" % "hamcrest-all" % "1.3" % "test",
        "org.mockito" % "mockito-all" % "1.9.5" % "test"),
      
      jmeURL := SVNURL parseURIEncoded "http://jmonkeyengine.googlecode.com/svn/trunk",
      jmeRevision := HEAD, // HEAD or rev(number)
      jmeDir := baseDirectory.value / "jme",
      antBaseDir := jmeDir.value / "engine",
      antBuildFile := antBaseDir.value / "build.xml",
      antTaskKey("jar") <<= antTaskKey("jar") dependsOn antTaskKey("clean"),
      logLevel in antTaskKey("jar") := Level.Error,
      logLevel in antTaskKey("clean") := Level.Error,
      logLevel in antStartServer := Level.Error,

      antServerLogger := { logger => new ProcessLogger {
        def buffer[T](f: ⇒ T): T = f
        def error(s: ⇒ String): Unit = logger warn s
        def info(s: ⇒ String): Unit = logger info s
      }},
      
      jmeClean := IO delete jmeDir.value,
      
      jmeJARs <<= jmeDir map distJARs dependsOn jmeBuild,
      
      unmanagedJars in Compile ++= (jmeJARs.value.classpath),
      
      jmeBuild <<= bind(antTaskKey("jar")) { buildTask =>
        (jmeCheckout, streams) flatMap { (base, out) =>
          val jars = distJARs(base)
          lazy val jarsModified = jars map (_ lastModified) min
          lazy val sourcesModified = (base / "engine" / "src" ***).get map (_ lastModified) max
          
          if (jars.isEmpty || sourcesModified > jarsModified) {
            out.log.info("Building JME from sources.")
            buildTask
          } else {
            out.log.debug("JME is up-to-date.")
            task(())
          }
        }
      } dependsOn streams,
      
      jmeCheckout <<= (jmeDir, jmeURL, jmeRevision, offline, streams) map { (work, repo, revision, offline, out) =>
        val manager = SVNClientManager.newInstance
        val updateClient = manager.getUpdateClient
        val wcClient = manager.getWCClient
        
        if (! work.exists) {
          out.log.info("Checking out JME from %s to %s - which may take a while." format (repo, work))
          updateClient.doCheckout(repo, work, revision, revision, INFINITY, true) 
        }
        
        val workURL = wcClient.doInfo(work, WORKING).getURL
        if (repo != workURL && ! offline) {
          out.log.info("Switching JME working copy from %s to %s - which may take a while." format (workURL, repo))
          updateClient.doSwitch(work, repo, revision, revision, INFINITY, true, true)
        }
        
        if (offline) out.log.debug("Not updating JME working copy because SBT is in offline mode.")
        else if (revision == wcClient.doInfo(work, WORKING).getRevision) out.log.debug("Not updating JME working copy because it is already at the desired revision.")
        else if (repo.getPath contains "/tags/") out.log.debug("Not updating JME working copy because it is a tag revision.")
        else {
          out.log.info("Updating JME working copy.")
          updateClient.doUpdate(work, revision, INFINITY, true, true)
        }
        
        work
      }
    )
  
  val jmeDir = settingKey[File]("directory where JME will be checked out to from SVN")
  val jmeURL = settingKey[SVNURL]("full URL (including branch/tag/trunk etc.) to the JME SVN repository")
  val jmeRevision = settingKey[SVNRevision]("indicates which revision of JME is used")
  val jmeCheckout = taskKey[File]("checks out JME from SVN and returns the working copy base directory")
  val jmeClean = taskKey[Unit]("deletes the JME sources")
  val jmeJARs = taskKey[Seq[File]]("builds JME from its sources and returns the engine JAR files")
  val jmeBuild = taskKey[Unit]("builds JME from sources if necessary")
  
  def distJARs(base: File) = base / "engine" / "dist" / "lib" ** "*.jar" get
}
