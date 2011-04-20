import sbt._
import sbt.FileUtilities.{ clean => delete, createDirectory, unzip }
import java.net.URL
import de.element34.sbteclipsify._

class EcidiceProject(info: ProjectInfo) extends DefaultProject(info) with Eclipsify with AkkaProject {
  val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"

  val joda = "joda-time" % "joda-time" % "1.6.2"
  
  val specs2 = "org.specs2" %% "specs2" % "1.2-SNAPSHOT" % "test"

  val jme = new URL("http://jmonkeyengine.com/nightly/jME3_2011-03-28.zip")
  
  override def compileOptions = super.compileOptions ++ Seq(Optimize, Deprecation, Unchecked)
  
  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")
  override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)
  override def includeTest(s: String) = s equals "ecidice.EcidiceSpec"
  system[String]("specs2.outDir") update (outputPath / "specs2").toString 
  override def testOptions = super.testOptions ++ Seq(TestArgument("html", "console"))
  
  lazy val fetchJme = task {
    val target = "lib" / "jme"
    delete(target, log)
    createDirectory(target, log)
    log.info("Fetching jME3 from " + jme)
    unzip(jme, target, AllPassFilter, log)
    None
  }
}