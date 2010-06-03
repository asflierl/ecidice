package ecidice.model
package mode

import ecidice.SpecBase

/**
 * Informal specification of any game mode that supports control requests.
 * 
 * @author Andreas Flierl
 */
class AnyModeSpec[A <: Mode[A]](game: A) extends SpecBase with TestHelpers {
  "Any mode" should {
    "correctly dupe itself" in {
      game.dupe() aka "the duped game" mustEqual game
    }
  }
}
object AnyModeSpec {
  def apply[A <: Mode[A]](game: A) = new AnyModeSpec(game)
}