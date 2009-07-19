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
    g = new Game
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
