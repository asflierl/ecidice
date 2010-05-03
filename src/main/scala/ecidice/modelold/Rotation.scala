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

package ecidice.modelold

/**
 * Describes the rotation state of a 6-side dice.
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
case class Rotation(top: Int, right: Int, front: Int) {
  def bottom = opposite(top)
  def left = opposite(right)
  def back = opposite(front)
  
  private def opposite(eyes: Int) = 7 - eyes
  
  def transform(how: Transform.Value) = how match {
    case Transform.ROTATE_BACKWARD => Rotation(front, right, bottom)
    case Transform.ROTATE_FORWARD => Rotation(back, right, top)
    case Transform.ROTATE_RIGHT => Rotation(left, top, front)
    case Transform.ROTATE_LEFT => Rotation(right, bottom, front)
    case Transform.SPIN_CLOCKWISE => Rotation(top, back, right)
    case Transform.SPIN_COUNTERCLOCKWISE => Rotation(top, front, left)
    case Transform.FLIP_UP_OR_DOWN => Rotation(bottom, right, back)
    case Transform.FLIP_LEFT_OR_RIGHT => Rotation(bottom, left, front)
  }
}
object Rotation {
  def initial = new Rotation(6, 5, 4)
}
