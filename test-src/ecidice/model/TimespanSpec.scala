/*
 * Copyright (c) 2009, Andreas Flierl
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

import ecidice.util.Preamble._

/**
 * Spec-based tests of the timespan model.
 * 
 * @author Andreas Flierl
 */
class TimespanSpec extends SpecBase {  
  "A timespan" should {
    
    var g : Game = new Game(1, new Board(3, 3))
    var ts : Timespan = new Timespan(g.clock, g.clock.now + 1d, 1d)
    
    "initially" >> { 
      "return the correct end time" in {
        ts.end mustEqual 2d
      }
      
      "display 0% progress directly after initialisation" in {
        ts.progress mustEqual 0f
      }
      
      "ignore negative values passed to 'lengthen'" in {
        ts lengthen(-2d)
        ts.end mustEqual 2d
        ts.progress mustEqual 0d
      }
    }
  
    "after some time elapsed" >> {
      "report 0% progress if the gametime is before the timespan start" in {
        g.update(.5f)
        ts.progress mustEqual 0f
      }
      
      "report 0% progress if the gametime equals the timespan start" in {
        g.update(1f)
        ts.progress mustEqual 0f
      }
      
      "report the correct progress if the gametime lies in the timespan" in {
        g.update(1f)
        for (x <- 0f to 1f step .1f) {
          ts.progress must beCloseTo(x, FLOAT_DELTA)
          g.update(.1f)
        }
      }
      
      "report 100% progress if the gametime equals the timespan end" in {
        g.update(2f)
        ts.progress mustEqual 1f
      }
      
      "report 100% progress if the gametime is after the timespan end" in {
        g.update(4f)
        ts.progress mustEqual 1f
      }
    }
  }
}
