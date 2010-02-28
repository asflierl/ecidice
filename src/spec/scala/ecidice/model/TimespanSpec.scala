/*
 * Copyright (c) 2009-2010, Andreas Flierl
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this 
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the names of the copyright holders nor the names of its 
 *   contributors may be used to endorse or promote products derived from this 
 *   software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ecidice.model

import org.specs._
import ecidice.SpecBase
import ecidice.util.Preamble._

/**
 * Spec-based tests of the timespan model.
 * 
 * @author Andreas Flierl
 */
object TimespanSpec extends SpecBase {  
  "A timespan" should {
    
    val clock = new Clock
    var ts = Timespan(clock, clock.now + 1d, 1d)
    
    "not start in the past, initially" in {
      Timespan(clock, clock.now - DOUBLE_DELTA, 1d) must throwAn[IllegalArgumentException]
    }
    
    "not point backwards in time" in {
      Timespan(clock, clock.now, -DOUBLE_DELTA) must throwAn[IllegalArgumentException]
    }
    
    "be able to represent a zero-second duration" in {
      val span = Timespan(clock, clock.now, 0)
      span.end mustEqual clock.now
    }
    
    "return the correct end time" in {
      ts.end mustEqual 2d
    }
    
    "display 0% progress right after initialisation" in {
      ts.progress mustEqual 0d
    }
  
    "report 0% progress if the current time is before the timespan start" in {
      clock.tick(.5f)
      ts.progress mustEqual 0f
    }
    
    "report 0% progress if the current time equals the timespan start" in {
      clock.tick(1d)
      ts.progress mustEqual 0d
    }
    
    "report the correct progress if the current time lies in the timespan" in {
      clock.tick(1d)
      for (x <- 0d to 1d by .001d) {
        ts.progress must beCloseTo(x, DOUBLE_DELTA)
        clock.tick(.001d)
      }
    }
    
    "report 100% progress if the current time equals the timespan end" in {
      clock.tick(2d)
      ts.progress mustEqual 1d
      
    }
    
    "report 100% progress if the current time is after the timespan end" in {
      clock.tick(4d)
      ts.progress mustEqual 1d
    }
    
    "report an accurate progress with large clock times" in {
      clock.tick(100d * 365d * 24d * 60d * 60d)
      ts = Timespan(clock, 4E-6d)
      clock.tick(3E-6d)

      ts.progress must beCloseTo(.75, DOUBLE_DELTA)
    }
  }
}
