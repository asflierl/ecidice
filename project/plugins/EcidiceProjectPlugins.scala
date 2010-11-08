import sbt._

 class EcidiceProjectPlugins(info: ProjectInfo) extends PluginDefinition(info) {
   lazy val eclipse = "de.element34" % "sbt-eclipsify" % "0.7.0"
   lazy val akka = "se.scalablesolutions.akka" % "akka-sbt-plugin" % "0.10"
 }