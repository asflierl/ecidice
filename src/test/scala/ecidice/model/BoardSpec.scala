/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

import ecidice.UnitSpec
import org.specs2._

object BoardSpec extends UnitSpec {
  val board = Board.sized(5, 3) 
    
  "The game board" should {
    "correctly indicate board bounds" in {
      
      "tile"     | "within bounds" |>
      Tile(0, 0) ! true            |
      Tile(4, 2) ! true            |
      Tile(1, 2) ! true            |
      Tile(4, 1) ! true            |
      Tile(5, 0) ! false           |
      Tile(0, 3) ! false           |
      Tile(5, 3) ! false           | { 
        
      (tile, result) =>
        board.contains(tile) must be equalTo result
      }
    }
    
    "know its tiles" in {
      val allTiles = (for (x <- 0 to 4; y <- 0 to 2) yield Tile(x, y)) toSet
      
      board.tiles must be equalTo allTiles
    }
  }
}
