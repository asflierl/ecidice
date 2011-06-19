/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

import Transform._
import scala.util.Random

/**
 * Describes a 6-side dice.
 *
 * The dice layout is 
 *  3
 *  6512
 *  4
 * 
 * where 6 is on top, 5 on the right side, 4 on the front. 
 * This can alternatively be represented as
 *  65
 *  4
 * 
 * to sufficiently describe the rotation of the dice.
 * 
 * @author Andreas Flierl
 */
case class Dice(top: Int, right: Int, front: Int) extends Contents {
  def bottom = opposite(top)
  def left = opposite(right)
  def back = opposite(front)
  
  private def opposite(eyes: Int) = 7 - eyes
  
  def transform(how: Transform.Value) = how match {
    case RotateBackward => Dice(front, right, bottom)
    case RotateForward => Dice(back, right, top)
    case RotateRight => Dice(left, top, front)
    case RotateLeft => Dice(right, bottom, front)
    case SpinClockwise => Dice(top, back, right)
    case SpinCounterclockwise => Dice(top, front, left)
    case FlipUpOrDown => Dice(bottom, right, back)
    case FlipLeftOrRight => Dice(bottom, left, front)
  }
}
object Dice {
  val default = Dice(6, 5, 4)
  
  val allRotations = next(default, 0, 0, List.empty)
  
  /**
   * Calculates all possible dice rotations in the following order where
   * `sc = SpinClockwise`, `rl = RotateLeft` and `rb = RotateBackward`, starting
   * with the initial `Dice(6, 5, 4)`.
   *
   * (6) sc, sc, sc, sc-rl, (5) sc, sc, sc, sc-rb,
   * (4) sc, sc, sc, sc-rl, (1) sc, sc, sc, sc-rb,
   * (2) sc, sc, sc, sc-rl, (3) sc, sc, sc, (sc-rb)
   */
  private def next(cur: Dice, spins: Int, rotas: Int, accu: List[Dice]): List[Dice] =
    if (spins < 3) next(spin(cur), spins + 1, rotas, cur :: accu)
    else if (spins == 3 && rotas == 5) (cur :: accu).reverse
    else next(spinrota(cur, rotas), 0, rotas + 1, cur :: accu)
  
  private def spin(d: Dice) = d.transform(SpinClockwise)
  
  private def spinrota(d: Dice, num: Int) =
    spin(d).transform(if (num % 2 == 0) RotateLeft else RotateBackward)
    
  def random = allRotations(Random.nextInt(allRotations.size))
}
