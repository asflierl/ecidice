/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package mode

import ecidice.UnitSpec
import time._

import org.specs2._

/**
 * Informal specification of the "Gauntlet" game mode.
 * 
 * @author Andreas Flierl
 */
object GauntletSpec extends AcceptanceSpec with ModelTestHelpers { def is = 
  "The game mode 'Gauntlet'".title ^
p^
    "...is specified by..." ^
p^
      AnyModeSpec() ^
      AnyModeWithControlRequestSpec() ^
      AnyModeWithRelinquishRequestSpec() ^
      AnyModeWithSpawningOfDiceSpec() ^
      AnyModeWithMovementSpec()
      
  implicit val game = Gauntlet.create(3)
}
