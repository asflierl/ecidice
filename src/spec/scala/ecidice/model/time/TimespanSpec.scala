/*
 * Copyright (c) 2009-2010 Andreas Flierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package ecidice.model
package time

import ecidice.SpecBase
import ecidice.util.Preamble._

/**
 * Informal specification of a timespan.
 * 
 * @author Andreas Flierl
 */
object TimespanSpec extends SpecBase {  
  "A timespan" should {
    "calculate a correct end instant if its duration is 0" in {
      Timespan(Instant(42), Duration(0)).end mustEqual Instant(42)
    }
    
    "calculate a correct end instant if its duration is > 0" in {
      Timespan(Instant(1), Duration(41)).end mustEqual Instant(42)
    }
    
    "display 0% progress if the current time is the start instant" in {
      Timespan(Instant(42), Duration(23)).progress(Instant(42)) mustEqual 0d
    }
  
    "report 0% progress if the current time is before the timespan start" in {
      Timespan(Instant(42), Duration(23)).progress(Instant(7)) mustEqual 0d
    }
    
    "report the correct progress if the current time lies in the timespan" in {
      val t = Timespan(Instant(0), Duration(1))
      
      for (x <- 0d to 1d by .001d) {
        t.progress(Instant(x)) must be closeTo(x +/- delta)
      }
    }
    
    "report 100% progress if the current time equals the timespan end" in {
      val t = Timespan(Instant(42), Duration(8))
      t.progress(Instant(50)) mustEqual 1d
      
    }
    
    "report 100% progress if the current time is after the timespan end" in {
      val t = Timespan(Instant(2), Duration(1))
      t.progress(Instant(42)) mustEqual 1d
    }
    
    "still be able to represent a microsecond duration after 100 years" in {
      val start = Instant(100d * 365d * 24d * 60d * 60d)
      val t = Timespan(start, Duration(1E-6d))
      val real = Duration.between(start, t.end).seconds
      
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
