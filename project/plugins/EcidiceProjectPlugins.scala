import sbt._

 class EcidiceProjectPlugins(info: ProjectInfo) extends PluginDefinition(info) {
   lazy val eclipse = "de.element34" % "sbt-eclipsify" % "0.6.0"
 }