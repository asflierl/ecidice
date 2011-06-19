/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

import ecidice.UnitSpec
import org.scalacheck._
import Prop.forAll
import Generators._
import Level._

import org.specs2._

object SpaceSpec extends UnitSpec {
  "A space on the game board" should {
    "be consistent in equivalence and ordering" in {
      check { 
        (a: Space, b: Space) => (a == b) must be equalTo ((a compare b) == 0)
      }
    }
    
    "have correct ordering behaviour" in {
      val spaces = List(Space(Tile(3, 1), Raised),
                        Space(Tile(5, 0), Floor),
                        Space(Tile(0, 1), Raised),
                        Space(Tile(3, 0), Floor))
      
      spaces.sortWith(_ < _) aka "the ordered spaces" must be equalTo (
          List(Space(Tile(3, 0), Floor),
               Space(Tile(5, 0), Floor),
               Space(Tile(0, 1), Raised),
               Space(Tile(3, 1), Raised))
      )
    }
    
    "indicate its level correctly" in {
      val tile = Tile(0, 0)
      
      "when on the floor" >> {
        val space = Space(tile, Floor)
        space.isFloor aka "is floor" must beTrue
        space.isRaised aka "is raised" must beFalse
      }
      
      "when on the raised level" >> {
        val space = Space(tile, Raised)
        space.isFloor aka "is floor" must beFalse
        space.isRaised aka "is raised" must beTrue
      }
    }
  }
}
