package ecidice.model

import ecidice.SpecBase

/**
 * Informal specification of a duration. 
 */
object DurationSpec extends SpecBase {
  "A duration" should {
    "take 0 seconds by default" in {
      Duration().seconds mustEqual 0d
    }
    
    "never be negative" in {
      Duration(-42) must throwAn[IllegalArgumentException]
    }
    
    "result in a new duration when a duration is added to it" in {
      Duration(42) + Duration(7) mustEqual Duration(49)
    }
    
    "result in an instant when an instant is added to it" in {
      Duration(42) + Instant(23) mustEqual Instant(65)
    }
    
    "correctly represent the duration between 2 instants" in {
      "instant a" | "instant b" | "result"     |>
      Instant(42) ! Instant(23) ! Duration(19) |
      Instant(11) ! Instant(42) ! Duration(31) |
      Instant(5)  ! Instant(5)  ! Duration(0)  | {
        (a, b, result) => Duration.between(a, b) mustEqual result
      }
    }
    
    "be comparable" in {
      "duration a" | "duration b" | "result" |>
      Duration(1)  ! Duration(0)  ! 1        |
      Duration(0)  ! Duration(1)  ! -1       |
      Duration(42) ! Duration(42) ! 0        | {
        (a, b, result) => a.compare(b) mustEqual result
      }
    }
  }
}