/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package time

import ecidice.UnitSpec

/**
 * Informal specification of an instant. 
 */
object InstantSpec extends UnitSpec {
  "An instant" should {
    "point to time zero by default" in {
      Instant().time must be equalTo 0d
    }
    
    "never be negative" in {
      Instant(-42) must throwAn[IllegalArgumentException]
    }
    
    "be comparable" in {
      "instant a" | "instant b" | "result" |>
      Instant(1)  ! Instant(0)  ! 1        |
      Instant(0)  ! Instant(1)  ! -1       |
      Instant(42) ! Instant(42) ! 0        | {
        (a, b, result) => a.compare(b) must be equalTo result
      }
    }
    
    "result in a new instant when a duration is added to it" in {
      Instant(42) + Duration(23) must be equalTo Instant(65)
    }
    
    "result in a new instant when a duration is subtracted from it" in {
      Instant(42) - Duration(23) must be equalTo Instant(19)
    }
    
    "not allow to subtract a duration greater than the instant" in {
      Instant(19) - Duration(23) must throwAn[IllegalArgumentException]
    }
    
    "correctly calculate the duration between 2 instants" in {
      "instant a" | "instant b" | "result"     |>
      Instant(42) ! Instant(23) ! Duration(19) |
      Instant(11) ! Instant(42) ! Duration(31) |
      Instant(5)  ! Instant(5)  ! Duration(0)  | {
        (a, b, result) => a - b must be equalTo result
      }
    }
  }
}
