/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package util

import com.jme3.system.AppSettings
import java.util.prefs.Preferences
import collection.JavaConversions._

import ecidice.i18n.L10n

class Prefs(root: Preferences) {
  private val jme = root.node("jME3")
  val settings = new AppSettings(true)

  for ((key, value) <- settings) (value: Any) match {
    case v: Int => settings.putInteger(key, jme.getInt(key, v))
    case v: Boolean => settings.putBoolean(key, jme.getBoolean(key, v))
    case v => settings.putString(key, jme.get(key, v.toString))
  }
  
  def save = {
    for ((key, value) <- settings) (value: Any) match {
      case v: Int => jme.putInt(key, v)
      case v: Boolean => jme.putBoolean(key, v)
      case v => jme.put(key, v.toString)
    }
    root.sync
  }
}
object Prefs {
  def load = new Prefs(Preferences.userRoot().node(L10n.of.appName))
  def uninstall = Preferences.userRoot().node(L10n.of.appName).removeNode()
}
