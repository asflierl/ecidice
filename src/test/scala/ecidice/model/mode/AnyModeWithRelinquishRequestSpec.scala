/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package mode

import ecidice.UnitSpec

/**
 * Informal specification of a player relinquishing control of a dice.
 * 
 * @author Andreas Flierl
 */
class AnyModeWithRelinquishRequestSpec[A <: Mode[A] with RelinquishRequest[A]](game: A) 
extends UnitSpec with ModelTestHelpers {
  
  "Any mode that allows a player to relinquish control of a dice" should {
    
    "allow relinquishing when the player is controlling a dice" in {
      val beforeControl = game.spawnPlayer(Tile(1, 1))
                         .addSolidDice(Tile(1, 1).floor)
                         
      val afterRelinquish =
        beforeControl.control(Player(1))
                     .relinquish(Player(1))
      
      afterRelinquish must be equalTo beforeControl
    }
    //TODO specifiy movement cases
  }
}
object AnyModeWithRelinquishRequestSpec {
  def apply[A <: Mode[A] with RelinquishRequest[A]]()(implicit game: A) = 
    new AnyModeWithRelinquishRequestSpec(game)
}
