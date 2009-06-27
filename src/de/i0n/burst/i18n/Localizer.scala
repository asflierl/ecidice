package de.i0n.burst.i18n

import de.i0n.concurrent._

/**
 * Provides a very basic localization service
 * 
 * @author Andreas Flierl
 * @see Localization
 */
@ThreadSafe
object Localizer {
  @volatile private var loc : Localization = English
  
  /**
   * Returns the current localization (default English).
   */
  def translate = loc
  
  /**
   * Sets the current localization to the given one.
   * 
   * @param loc the localization to set as the current one
   */
  def setLocalization(loc : Localization) {
    this.loc = loc
  }
}
