/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package time

import ecidice.UnitSpec

/**
 * Informal specification of a timespan.
 * 
 * @author Andreas Flierl
 */
object TimespanSpec extends UnitSpec {  
  "A timespan" should {
    "calculate a correct end instant if its duration is 0" in {
      Timespan(Instant(42), Duration(0)).end must be equalTo Instant(42)
    }
    
    "calculate a correct end instant if its duration is > 0" in {
      Timespan(Instant(1), Duration(41)).end must be equalTo Instant(42)
    }
    
    "display 0% progress if the current time is the start instant" in {
      Timespan(Instant(42), Duration(23)).progress(Instant(42)) must be equalTo 0d
    }
  
    "report 0% progress if the current time is before the timespan start" in {
      Timespan(Instant(42), Duration(23)).progress(Instant(7)) must be equalTo 0d
    }
    
    "report the correct progress if the current time lies in the timespan" in {
      val t = Timespan(Instant(0), Duration(1))
      
      (0d to 1d by .001d) map {
        x => t.progress(Instant(x)) must be closeTo(x +/- delta)
      } reduceLeft (_ and _)
    }
    
    "report 100% progress if the current time equals the timespan end" in {
      val t = Timespan(Instant(42), Duration(8))
      t.progress(Instant(50)) must be equalTo 1d
      
    }
    
    "report 100% progress if the current time is after the timespan end" in {
      val t = Timespan(Instant(2), Duration(1))
      t.progress(Instant(42)) must be equalTo 1d
    }
    
    "still be able to represent a microsecond duration after 100 years" in {
      val start = Instant(100d * 365d * 24d * 60d * 60d)
      val t = Timespan(start, Duration(1E-6d))
      val real = (t.end - start).seconds
      
      real must beCloseTo(1E-6d +/- 5E-7d)
    }
    
    "report an accurate progress with large clock times" in {
      val start = Instant(100d * 365d * 24d * 60d * 60d)
      val t = Timespan(start, Duration(4E-6d))
      val now = start + Duration(3E-6d)

      t.progress(now) must be closeTo(.75 +/- delta)
    }
  }
}
