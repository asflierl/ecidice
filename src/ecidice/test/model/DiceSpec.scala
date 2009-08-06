package ecidice.test.model

import ecidice.model._

/**
 * Tests the dice model.
 * 
 * @author Andreas Flierl
 */
class DiceSpec extends TestBase {
  
  private var d : Dice = _
  
  override def beforeEach() = {
    d = new Dice
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
      d.change(Transform.ROTATE_UP)
      checkDice(4, 5, 1)
    }
    
    it("should correctly rotate downwards (towards the front)") {
      d.change(Transform.ROTATE_DOWN)
      checkDice(3, 5, 6)
    }
    
    it("should correctly rotate to the right") {
      d.change(Transform.ROTATE_RIGHT)
      checkDice(2, 6, 4)
    }
    
    it("should correctly rotate to the left") {
      d.change(Transform.ROTATE_LEFT)
      checkDice(5, 1, 4)
    }
    
    it("should correctly spin clockwise") {
      d.change(Transform.SPIN_CLOCKWISE)
      checkDice(6, 3, 5)
    }
    
    it("should correctly spin counter-clockwise") {
      d.change(Transform.SPIN_COUNTERCLOCKWISE)
      checkDice(6, 4, 2)
    }
    
    it("should correctly flip up/down") {
      d.change(Transform.FLIP_UP_OR_DOWN)
      checkDice(1, 5, 3)
    }
    
    it("should correctly flip left or right") {
      d.change(Transform.FLIP_LEFT_OR_RIGHT)
      checkDice(1, 2, 4)
    }
  }
}
