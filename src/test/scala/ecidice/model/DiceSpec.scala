/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

import ecidice.UnitSpec
import Transform._

import org.specs2._

object DiceSpec extends UnitSpec {
  "A dice" should {
    val dice = Dice.default
    
    "initially look like this: top = 6, right = 5, front = 4" in {
      dice.top must be equalTo 6 
      dice.right must be equalTo 5
      dice.front must be equalTo 4
    }
    
    "initially be consistent " in {
      dice.top must be equalTo (7 - dice.bottom)
      dice.left must be equalTo (7 - dice.right)
      dice.front must be equalTo (7 - dice.back)
    }
    
    "know about all different dice rotations" in {
      all.size must be equalTo 24
    }
    
    "correctly rotate backward" in {
      val changed = dice.transform(RotateBackward)
      check(changed, 4, 5, 1)
    }
    
    "correctly rotate forward" in {
      val changed = dice.transform(RotateForward)
      check(changed, 3, 5, 6)
    }
    
    "correctly rotate to the right" in {
      val changed = dice.transform(RotateRight)
      check(changed, 2, 6, 4)
    }
    
    "correctly rotate to the left" in {
      val changed = dice.transform(RotateLeft)
      check(changed, 5, 1, 4)
    }
    
    "correctly spin clockwise" in {
      val changed = dice.transform(SpinClockwise)
      check(changed, 6, 3, 5)
    }
    
    "correctly spin counter-clockwise" in {
      val changed = dice.transform(SpinCounterclockwise)
      check(changed, 6, 4, 2)
    }
    
    "correctly flip up/down" in {
      val changed = dice.transform(FlipUpOrDown)
      check(changed, 1, 5, 3)
    }
    
    "correctly flip left or right" in {
      val changed = dice.transform(FlipLeftOrRight)
      check(changed, 1, 2, 4)
    }
  }
  
  def check(changed: Dice, top: Int, right: Int, front: Int) = {
    (changed.top must be equalTo top) and
    (changed.right must be equalTo right) and
    (changed.front must be equalTo front) and
    (changed.bottom must be equalTo (7 - top)) and
    (changed.left must be equalTo (7 - right)) and
    (changed.back must be equalTo (7 - front)) and
    (all must contain(changed))
  }
  
  val all = Dice.allRotations.toSet
}
