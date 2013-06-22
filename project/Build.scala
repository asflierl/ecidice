import sbt._
import Keys._
import Project.Setting
import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseKeys
import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseCreateSrc
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.wc.SVNClientManager
import org.tmatesoft.svn.core.wc.SVNRevision.{HEAD, WORKING}
import org.tmatesoft.svn.core.SVNDepth.INFINITY
import de.johoop.ant4sbt.Ant4Sbt._
import Project.{bind, value}

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
      scalacOptions ++= Seq("-deprecation", "-unchecked", "-optimise", "-language:_", "-encoding", "UTF-8"),
      autoCompilerPlugins := true,
      addCompilerPlugin("org.scala-lang.plugins" % "continuations" % "2.10.2"),
      
      fork in run := true,
      mainClass in (Compile, run) := Some("ecidice.Main"),
      
      EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource,
      
      testOptions := Seq(
        Tests.Filter(_ == "index"), 
        Tests.Argument("html", "console")),
  
      testOptions <+= crossTarget map { ct =>
        Tests.Setup { () => 
          sys.props += "specs2.outDir" -> (ct / "specs2" absolutePath)
        }
      },
  
      libraryDependencies ++= Seq(
        "org.scalaz" %% "scalaz-core" % "7.0.0",
        "org.scalaz" %% "scalaz-effect" % "7.0.0",
        "com.chuusai" %% "shapeless" % "1.2.4",
        "com.github.nscala-time" %% "nscala-time" % "0.4.0",
        "org.spire-math" %% "spire" % "0.4.0",
        "com.typesafe.akka" %% "akka-actor" % "2.1.2",
        
        "org.specs2" %% "specs2" % "2.0" % "test",
        "org.scalacheck" %% "scalacheck" % "1.10.1" % "test",
        "junit" % "junit" % "4.7" % "test",
        "org.pegdown" % "pegdown" % "1.2.1" % "test",
        "org.hamcrest" % "hamcrest-all" % "1.1" % "test",
        "org.mockito" % "mockito-all" % "1.9.5" % "test"),
  
      unmanagedJars in Compile <++= jmeJARs map (_ classpath),
      
      offline := false,
      
      jmeJARs <<= jmeDir map distJARs dependsOn jmeBuild,
      
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
      
      antBaseDir <<= jmeDir / "engine",
      
      antBuildFile <<= antBaseDir / "build.xml",

      antTaskKey("jar") <<= antTaskKey("jar") dependsOn antTaskKey("clean"),
      
      logLevel in antTaskKey("jar") := Level.Error,
      logLevel in antTaskKey("clean") := Level.Error,
      logLevel in antStartServer := Level.Error,
      
      jmeCheckout <<= (jmeDir, jmeURL, offline, streams) map { (work, repo, offline, out) =>
        val manager = SVNClientManager.newInstance
        val updateClient = manager.getUpdateClient
        val wcClient = manager.getWCClient
        
        if (! work.exists) {
          out.log.info("Checking out JME from %s to %s - which may take a while." format (repo, work))
          updateClient.doCheckout(repo, work, HEAD, HEAD, INFINITY, true) 
        }
        
        val workURL = wcClient.doInfo(work, WORKING).getURL
        if (repo != workURL && ! offline) {
          out.log.info("Switching JME working copy from %s to %s - which may take a while." format (workURL, repo))
          updateClient.doSwitch(work, repo, HEAD, HEAD, INFINITY, true, true)
        }
        
        if (offline) out.log.debug("Not updating JME working copy because SBT is in offline mode.")
        else if (repo.getPath contains "/tags") out.log.debug("Not updating JME working copy because it is a tag revision.")
        else {
          out.log.info("Updating JME working copy.")
          updateClient.doUpdate(work, HEAD, INFINITY, true, true)
        }
        
        work
      },
      
      jmeClean <<= jmeDir map IO.delete,
      
      jmeURL := SVNURL parseURIEncoded "http://jmonkeyengine.googlecode.com/svn/trunk",
      
      jmeDir <<= baseDirectory / "jme",
      
      resolvers ++= Seq(
        "Typesafe Repository"   at "http://repo.typesafe.com/typesafe/releases/",
        "Sonatype Snapshots"    at "http://oss.sonatype.org/content/repositories/snapshots",
        "Sonatype Releases"     at "http://oss.sonatype.org/content/repositories/releases")
    )
  
  val jmeDir = SettingKey[File]("jme-dir", "directory where JME will be checked out to from SVN")
  val jmeURL = SettingKey[SVNURL]("jme-url", "full URL (including branch/tag/trunk etc.) to the JME SVN repository")
  val jmeCheckout = TaskKey[File]("jme-checkout", "checks out JME from SVN and returns the working copy base directory")
  val jmeClean = TaskKey[Unit]("jme-clean", "deletes the JME sources")
  val jmeJARs = TaskKey[Seq[File]]("jme-jars", "builds JME from its sources and returns the engine JAR files")
  val jmeBuild = TaskKey[Unit]("jme-build", "builds JME from sources if necessary")
  
  def distJARs(base: File) = base / "engine" / "dist" / "lib" ** "*.jar" get
}
