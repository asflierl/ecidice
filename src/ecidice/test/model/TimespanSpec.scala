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

package ecidice.test.model

import ecidice.model._
import ecidice.util.Preamble._

/**
 * Tests the timespan model class.
 * 
 * @author Andreas Flierl
 */
class TimespanSpec extends TestBase {
  
  private var g : Game = _
  private var ts : Timespan = _
  
  override def beforeEach() = {
    g = new Game(1)
    ts = new Timespan(g, g.now + 1f, 1f)
  }
  
  describe("A Timespan object") {
    
    describe("when newly created") {
      it("should return the correct end time") {
        ts.end should be (2f)
      }
      
      it("should display 0% progress directly after initialisation") {
        ts.progress should be (0f)
      }
      
      it("should ignore negative values passed to 'lengthen'") {
        ts.lengthen(-2f)
        ts.end should be (2f)
        ts.progress should be (0f)
      }
    }
  
    describe("after some time elapsed") {
      it("should report 0% progress if the gametime is before the timespan start") {
        g.update(.5f)
        ts.progress should be (0f)
      }
      
      it("should report 0% progress if the gametime equals the timespan start") {
        g.update(1f)
        ts.progress should be (0f)
      }
      
      it("should report the correct progress if the gametime lies in the timespan") {
        g.update(1f)
        for (x <- 0f to 1f step .1f) {
          ts.progress should be (x plusOrMinus DELTA)
          g.update(.1f)
        }
      }
      
      it("should report 100% progress if the gametime equals the timespan end") {
        g.update(2f)
        ts.progress should be (1f)
      }
      
      it("should report 100% progress if the gametime is after the timespan end") {
        g.update(4f)
        ts.progress should be (1f)
      }
    }
  }
}
