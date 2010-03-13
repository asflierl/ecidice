/*
 * Copyright (c) 2009-2010, Andreas Flierl
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

package ecidice.model.dice

import ecidice.model._
import ecidice.util._

/**
 * A single 6-side dice.
 * <p>
 * The dice layout is 
 * <pre>
 *  3
 *  6512
 *  4
 * </pre>
 * where 6 is on top, 5 on the right side, 4 on the front. 
 * This can alternatively be represented as
 * <pre>
 *  65
 *  4
 * </pre>
 * to sufficiently describe the rotation of the dice.
 * <p>
 * This class is final because it is not designed for extension through 
 * inheritance (esp. considering equals(Any)).
 * 
 * @author Andreas Flierl
 */
abstract class Dice [A <: Dice[_]] protected (
    rotation: Rotation,
    private[dice] val serial: Long) 
{ this: A =>
  
  def top = rotation.top
  def bottom = opposite(top)
  def right = rotation.right
  def left = opposite(right)
  def front = rotation.front
  def back = opposite(front)
  
  private def opposite(eyes: Int) = 7 - eyes
  
  protected def create(newRotation: Rotation): A
  
  def change(how: Transform.Value) = {
    how match {
      case Transform.ROTATE_BACKWARD => create(Rotation(front, right, bottom))
      case Transform.ROTATE_FORWARD => create(Rotation(back, right, top))
      case Transform.ROTATE_RIGHT => create(Rotation(left, top, front))
      case Transform.ROTATE_LEFT => create(Rotation(right, bottom, front))
      case Transform.SPIN_CLOCKWISE => create(Rotation(top, back, right))
      case Transform.SPIN_COUNTERCLOCKWISE => create(Rotation(top, front, left))
      case Transform.FLIP_UP_OR_DOWN => create(Rotation(bottom, right, back))
      case Transform.FLIP_LEFT_OR_RIGHT => create(Rotation(bottom, left, front))
    }
  }
  
  override def equals(obj: Any) = obj match {
    case other: Dice[_] => other.serial == serial
    case _ => false
  }
  
  override def hashCode = HashCode(serial)
  
  override def toString = "Dice[%d](%d-%d-%d)".format(serial, top, right, front)
}

object Dice {//TODO not thread-safe, might need a fix in the context of more threads
  private var serial = 0L

  private[dice] def nextSerial = {
    val before = serial
    serial += 1L
    before
  }
  
//  def appear()
}
