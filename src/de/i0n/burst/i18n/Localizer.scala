package de.i0n.burst.i18n

object Localizer {
  @volatile private var loc : Localization = English
  
  def translate = loc
  
  def setLocalization(loc : Localization) {
    this.loc = loc
  }
}
