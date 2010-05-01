package ecidice.model
package time

import ecidice.SpecBase

/**
 * Informal specification of an instant. 
 */
object InstantSpec extends SpecBase {
  "An instant" should {
    "point to time zero by default" in {
      Instant().time mustEqual 0d
    }
    
    "never be negative" in {
      Instant(-42) must throwAn[IllegalArgumentException]
    }
    
    "be comparable" in {
      "instant a" | "instant b" | "result" |>
      Instant(1)  ! Instant(0)  ! 1        |
      Instant(0)  ! Instant(1)  ! -1       |
      Instant(42) ! Instant(42) ! 0        | {
        (a, b, result) => a.compare(b) mustEqual result
      }
    }
    
    "result in a new instant when a duration is added to it" in {
      Instant(42) + Duration(23) mustEqual Instant(65)
    }
  }
}