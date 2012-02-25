/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

import ecidice.UnitSpec
import org.scalacheck._
import Generators._

import org.specs2._

object TileSpec extends UnitSpec {
  "A tile" should {
    "be consistent in equivalence and ordering" in {
      check {
        (a: Tile, b: Tile) => (a == b) must be equalTo ((a compare b) == 0)
      }
    }
    
    "have correct ordering behaviour" in {
      val tiles = List(Tile(3, 1), Tile(-1, -2), Tile(4, 0), Tile(0, 1), 
          Tile(-1, 2), Tile(3, 5), Tile(3, -2), Tile(2, 0))
      
      tiles.sorted aka "the ordered tiles" must be equalTo (
          List(Tile(-1, -2), Tile(3, -2), Tile(2, 0), Tile(4, 0), Tile(0, 1), 
              Tile(3, 1), Tile(-1, 2), Tile(3, 5))
      )
    }
    
    "return the correct tile when looking in any direction" in {
      
      "direction"         | "result position" |>
      Direction.Backward  ! Tile(2, 2)        |
      Direction.Forward   ! Tile(2, 0)        |
      Direction.Left      ! Tile(1, 1)        |  
      Direction.Right     ! Tile(3, 1)        | { 
        
      (dir, result) =>
        Tile(2, 1).look(dir) must be equalTo(result)
      }
    }
  }
}
