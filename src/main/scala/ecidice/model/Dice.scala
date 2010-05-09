/*
 * Copyright (c) 2009-2010 Andreas Flierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package ecidice.model

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
  def initial = Dice(6, 5, 4)
  
  val allRotations = next(initial, 0, 0, List.empty)
  
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
