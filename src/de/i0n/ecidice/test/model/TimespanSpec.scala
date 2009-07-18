package de.i0n.ecidice.test.model

import org.scalatest._
import org.scalatest.matchers._

import de.i0n.ecidice.model.Timespan

/**
 * Tests the timespan model class.
 * 
 * @author Andreas Flierl
 */
class TimespanSpec extends Spec with ShouldMatchers with BeforeAndAfter 
    with BurstTest {
  
  private var ts : Timespan = _
  
  override def beforeEach() = {
    ts = new Timespan(1f)
  }
  
  describe("A Timespan object") {
    
    it("should return the time-to-live it was created with") {
      ts.timeToLive should be (1f plusOrMinus DELTA)
    }
    
    it("should display 0% progress directly after initialisation") {
      ts.progress should be (0f plusOrMinus DELTA)
    }
    
    it("should correctly subtract any elapsed time") {
      ts.elapse(1f/3f)
      ts.timeToLive should be (2f/3f plusOrMinus DELTA)
    }
    
    it("should correctly display the total elapsed time") {
      ts.elapse(.2f)
      ts.elapse(.3f)
      ts.elapsed should be (.5f plusOrMinus DELTA)
    }
    
    it("should ignore negative elapsed time values") {
      ts.elapse(-2f)
      ts.timeToLive should be (1f plusOrMinus DELTA)
      ts.progress should be (0f plusOrMinus DELTA)
    }
    
    it("should correctly update its progress after some time elapsed") {
      ts.elapse(1f/3f)
      ts.progress should be (1f/3f plusOrMinus DELTA)
    }
    
    it("should correctly add any delay time") {
      ts.delay(9f)
      ts.timeToLive should be (10f plusOrMinus DELTA)
    }
    
    it("should correctly display the total delay time") {
      ts.delay(2f)
      ts.delay(1f)
      ts.delayed should be (3f plusOrMinus DELTA)
    }
    
    it("should ignore negative delay time values") {
      ts.delay(-2f)
      ts.timeToLive should be (1f plusOrMinus DELTA)
      ts.progress should be (0f plusOrMinus DELTA)
    }
    
    it("should display 0% progress after initialisation and some delay") {
      ts.delay(10f/3f)
      ts.progress should be (0f plusOrMinus DELTA)
    }
    
    it("should correctly calculate its progress after elapsed time and delays") {
      ts.delay(11f)
      ts.elapse(2f)
      ts.delay(6f)
      ts.elapse(4f)
      ts.progress should be (1f/3f plusOrMinus DELTA)
    }
  }
}
