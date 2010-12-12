import sbt._
import sbt.FileUtilities.{ clean => delete, createDirectory, unzip }
import java.net.URL
import de.element34.sbteclipsify._

class EcidiceProject(info: ProjectInfo) extends DefaultProject(info) with Eclipsify with AkkaProject {
  val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"
  
  val specs = "org.scala-tools.testing" %% "specs" % "1.6.6" % "test"
  val scalacheck = "org.scala-tools.testing" %% "scalacheck" % "1.7"
  val mockito = "org.mockito" % "mockito-all" % "1.8.5"
  val cglib = "cglib" % "cglib" % "2.1_3"
  val asm = "asm" % "asm" % "1.5.3"
  val objenesis = "org.objenesis" % "objenesis" % "1.0"
  val hamcrest = "org.hamcrest" % "hamcrest-all" % "1.1"
  val junit = "junit" % "junit" % "4.7"

  val jme = new URL("http://jmonkeyengine.com/nightly/jME3_12-12-2010.zip")
  
  override def compileOptions = super.compileOptions ++ Seq(Optimize)
  
  override def testOptions = super.testOptions ++ Seq(TestFilter(_ == "ecidice.CompositeSpec"))
  
  lazy val fetchJme = task {
    val target = "lib" / "jme"
    delete(target, log)
    createDirectory(target, log)
    log.info("Fetching jME3 from " + jme)
    unzip(jme, target, AllPassFilter, log)
    None
  }
}