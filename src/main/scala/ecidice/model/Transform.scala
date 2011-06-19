/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

import Direction._

/**
 * Lists possible transformations of a dice.
 * 
 * @author Andreas Flierl
 */
object Transform extends Enumeration {
  val RotateBackward = Value("rotate backward")
  val RotateForward = Value("rotate forward")
  val RotateLeft = Value("rotate left")
  val RotateRight = Value("rotate right")
  val SpinClockwise = Value("spin clockwise")
  val SpinCounterclockwise = Value("spin counterclockwise")
  val FlipUpOrDown = Value("flip up or down")
  val FlipLeftOrRight = Value("flip left or right")
  
  def apply(from: Space, to: Space, dir: Direction.Value): Transform.Value = {
    if (from.isFloor == to.isFloor) dir match {
      case Backward => RotateBackward
      case Forward => RotateForward
      case Right => RotateRight
      case Left => RotateLeft
    } else dir match {
      case Backward | Forward => FlipUpOrDown
      case Left | Right => FlipLeftOrRight
    }
  }
}
