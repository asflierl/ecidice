/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice

import org.specs2._
import org.junit.runner._
import runner._

@RunWith(classOf[JUnitRunner])
class EcidiceSpec extends AcceptanceSpec { def is =
  "ecidice".title                                                                                  ^
                                                                                                  p^
    "ecidice consists of"                                                                          ^
                                                                                                  p^
      "the game board" ~ model.BoardSpec                                                           ^
      "dice" ~ model.DiceSpec                                                                      ^
      "the dice matcher" ~ model.DiceMatcherSpec                                                   ^
      "spaces on the game board" ~ model.SpaceSpec                                                 ^
      "the tiles of the board" ~ model.TileSpec                                                    ^
                                                                                                  p^
      "the game mode 'Gauntlet'" ~ model.mode.GauntletSpec                                         ^
                                                                                                  p^    
      "durations" ~ model.time.DurationSpec                                                        ^
      "timespans" ~ model.time.TimespanSpec                                                        ^
      "instants" ~ model.time.InstantSpec                                                          ^
                                                                                                  p^
      "a hash code utility" ~ util.HashCodeSpec
}
