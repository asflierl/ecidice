/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package i18n


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
  @volatile private var loc: L10n = English
  
  /**
   * Returns the current localization (default English).
   */
  def of = loc
  
  def set(loc: L10n) {
    this.loc = loc
  }
}
