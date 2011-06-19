/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package time

import ecidice.UnitSpec

/**
 * Informal specification of a duration. 
 */
object DurationSpec extends UnitSpec {
  "A duration" should {
    "take 0 seconds by default" in {
      Duration().seconds must be equalTo 0d
    }
    
    "never be negative" in {
      Duration(-42) must throwAn[IllegalArgumentException]
    }
    
    "result in a new duration when a duration is added to it" in {
      Duration(42) + Duration(7) must be equalTo Duration(49)
    }
    
    "result in an instant when an instant is added to it" in {
      Duration(42) + Instant(23) must be equalTo Instant(65)
    }
    
    "be comparable" in {
      "duration a" | "duration b" | "result" |>
      Duration(1)  ! Duration(0)  ! 1        |
      Duration(0)  ! Duration(1)  ! -1       |
      Duration(42) ! Duration(42) ! 0        | {
        (a, b, result) => a.compare(b) must be equalTo result
      }
    }
  }
}
