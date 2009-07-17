package de.i0n.burst.test.model

import org.scalatest._
import org.scalatest.matchers._

import de.i0n.burst.model._

class DiceSpec extends Spec with ShouldMatchers with BeforeAndAfter 
    with BurstTest {
  
  private val t = new Tile(0, 0)
  private var d : Dice = _
  
  override def beforeEach() = {
    d = Dice(t.floor)
  }
  
  def checkDice(top: Int, right: Int, front: Int) = {
    d.top should be (top)
    d.right should be (right)
    d.front should be (front)
    d.bottom should be (7 - top)
    d.left should be (7 - right)
    d.back should be (7 - front)
  }
  
  describe("A dice") {
    
    it("should initially be t6 r5 f4") {
      d.top should be (6)
      d.right should be (5)
      d.front should be (4)
    }
    
    it("should initially be consistent") {
      d.top should equal (7 - d.bottom)
      d.left should equal (7 - d.right)
      d.front should equal (7 - d.back)
    }

    it("should correctly rotate upwards (towards the back)") {
      d.rotateUp()
      checkDice(4, 5, 1)
    }
    
    it("should correctly rotate downwards (towards the front)") {
      d.rotateDown()
      checkDice(3, 5, 6)
    }
    
    it("should correctly rotate to the right") {
      d.rotateRight()
      checkDice(2, 6, 4)
    }
    
    it("should correctly rotate to the left") {
      d.rotateLeft()
      checkDice(5, 1, 4)
    }
    
    it("should correctly spin clockwise") {
      d.spinClockwise()
      checkDice(6, 3, 5)
    }
    
    it("should correctly spin counter-clockwise") {
      d.spinCounterClockwise()
      checkDice(6, 4, 2)
    }
    
    it("should correctly flip up/down") {
      d.flipUpOrDown()
      checkDice(1, 5, 3)
    }
    
    it("should correctly flip left or right") {
      d.flipLeftOrRight()
      checkDice(1, 2, 4)
    }
  }
}
