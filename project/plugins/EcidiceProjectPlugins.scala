import sbt._

class EcidiceProjectPlugins(info: ProjectInfo) extends PluginDefinition(info) {
  val akkaRepo = "akka repo" at "http://www.scalablesolutions.se/akka/repository"
 
  val eclipse = "de.element34" % "sbt-eclipsify" % "0.7.0"
  val akka = "se.scalablesolutions.akka" % "akka-sbt-plugin" % "1.0-RC1"
}
