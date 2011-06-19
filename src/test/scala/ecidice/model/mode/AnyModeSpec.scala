/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package mode

import ecidice.UnitSpec

/**
 * Informal specification of basic stuff in any game mode.
 * 
 * @author Andreas Flierl
 */
class AnyModeSpec[A <: Mode[A]](game: A) extends UnitSpec with ModelTestHelpers {
  "Any mode" should {
    "correctly dupe itself" in {
      game.dupe() aka "the duped game" must be equalTo game
    }
    
    "correctly spawn new players" in {
      val gameWithOnePlayer = game.spawnPlayer(Tile(0, 0))
      val gameWithTwoPlayers = gameWithOnePlayer.spawnPlayer(Tile(2, 1))
      
      gameWithTwoPlayers.players must be equalTo 
        Map(Player(1) -> Standing(Tile(0, 0)),
            Player(2) -> Standing(Tile(2, 1)))
    }
  }
}
object AnyModeSpec {
  def apply[A <: Mode[A]]()(implicit game: A) = new AnyModeSpec(game)
}
