import sbt._
import de.element34.sbteclipsify._

class EcidiceProject(info: ProjectInfo) extends DefaultProject(info) with Eclipsify {
  val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"
  
  val specs = "org.scala-tools.testing" % "specs_2.8.0.RC7" % "1.6.5-SNAPSHOT"
  val scalacheck = "org.scala-tools.testing" % "scalacheck_2.8.0.RC7" % "1.8-SNAPSHOT"
  val mockito = "org.mockito" % "mockito-all" % "1.8.4"
  val cglib = "cglib" % "cglib" % "2.1_3"
  val asm = "asm" % "asm" % "1.5.3"
  val objenesis = "org.objenesis" % "objenesis" % "1.1"
  val hamcrest = "org.hamcrest" % "hamcrest-all" % "1.1"
  val junit = "junit" % "junit" % "4.8.1"
  
  override def compileOptions = super.compileOptions ++ Seq(Optimize)
  
  override def testOptions = super.testOptions ++ Seq(TestFilter(_ == "ecidice.CompositeSpec"))
}