/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package i18n

import scala.concurrent.SyncVar


/**
 * Defines the words and phrases that require localization.
 * 
 * @author Andreas Flierl
 */
trait L10n {
  def appName: String
  def windowTitle: String
}

/**
 * Provides a very basic localization service
 * 
 * @author Andreas Flierl
 */
object L10n {
  private val loc = new SyncVar[L10n]()
  
  loc set English
  
  /**
   * Returns the current localization (default English).
   */
  def of = loc get
  
  def set(l: L10n) = loc set l
}
