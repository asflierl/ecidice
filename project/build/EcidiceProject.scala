import sbt._
import sbt.FileUtilities.{ clean => delete, createDirectory, unzip }
import java.net.URL
import de.element34.sbteclipsify._

class EcidiceProject(info: ProjectInfo) extends DefaultProject(info) with Eclipsify with AkkaProject {
  val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"

  val joda = "joda-time" % "joda-time" % "1.6.2"
  
  val specs2 = "org.specs2" %% "specs2" % "1.2-SNAPSHOT" % "test"
  val mockito = "org.mockito" % "mockito-all" % "1.8.5" % "test"
  val hamcrest = "org.hamcrest" % "hamcrest-all" % "1.1" % "test"

  val jme = new URL("http://jmonkeyengine.com/nightly/jME3_2011-03-28.zip")
  
  override def compileOptions = super.compileOptions ++ Seq(Optimize)
  
  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")
  override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)
  override def includeTest(s: String) = { s == "ecidice.EcidiceSpec" }
  
  lazy val fetchJme = task {
    val target = "lib" / "jme"
    delete(target, log)
    createDirectory(target, log)
    log.info("Fetching jME3 from " + jme)
    unzip(jme, target, AllPassFilter, log)
    None
  }
}