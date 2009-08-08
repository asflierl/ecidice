/*
 * Copyright (c) 2009, Andreas Flierl
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
 * - Neither the name "ecidice" nor the names of its contributors may be used to
 *   endorse or promote products derived from this software without specific
 *   prior written permission.
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

package ecidice.model

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
 * 
 * @author Andreas Flierl
 */
class Dice {
  private var topFace = 6
  private var rightFace = 5
  private var frontFace = 4
  
  /**
   * Holds the state of this dice in respect to the game rules.
   */
  var state : Dice.State = _
  
  /** Returns the number of eyes on the top face. */
  def top = topFace
  
  /** Returns the number of eyes on the bottom face. */
  def bottom = opposite(topFace)
  
  /** Returns the number of eyes on the right face. */
  def right = rightFace
  
  /** Returns the number of eyes on the left face. */
  def left = opposite(rightFace)
  
  /** Returns the number of eyes on the front face. */
  def front = frontFace
  
  /** Returns the number of eyes on the back face. */
  def back = opposite(frontFace)
  
  /**
   * Returns the number on the opposite site of the specified face, i.e. 
   * <code>7 - eyes</code>.
   * 
   * @param eyes the eyes on the face whose opposite to return
   * @return the number of eyes on the opposite side
   */
  private def opposite(eyes: Int) = 7 - eyes
  
  /**
   * Assigns this dice's top, right and front faces. No consistency checking is
   * done by this method.
   * 
   * @param t the new top face
   * @param r the new right face
   * @param f the new front face
   */
  private def set(t: Int, r: Int, f: Int) {
    topFace = t
    rightFace = r
    frontFace = f
  }
  
  /**
   * Changes this dice's rotation according to the specified transform.
   * 
   * @param how the transform to apply
   */
  def change(how: Transform.Value) = how match {
    case Transform.ROTATE_UP => set(front, right, bottom)
    case Transform.ROTATE_DOWN => set(back, right, top)
    case Transform.ROTATE_RIGHT => set(left, top, front)
    case Transform.ROTATE_LEFT => set(right, bottom, front)
    case Transform.SPIN_CLOCKWISE => set(top, back, right)
    case Transform.SPIN_COUNTERCLOCKWISE => set(top, front, left)
    case Transform.FLIP_UP_OR_DOWN => set(bottom, right, back)
    case Transform.FLIP_LEFT_OR_RIGHT => set(bottom, left, front)
  }
}
object Dice {    
  /**
   * Supertype of a dice's possible states.
   */
  sealed abstract class State
  
  /**
   * The dice is appearing. It can not be controlled nor moved. It occupies 
   * some space.
   */
  case class Appearing(where: Space, when: Timespan) extends State with Timed
  
  /**
   * The dice is solid now. It can be controlled by a player. It occupies
   * some space.
   */
  case class Solid(where: Space, controller: Option[Player]) extends State
  
  /**
   * The dice is moving. During movement, it is always controlled by a player.
   * The movement object defines the space occupied while moving.
   */
  case class Moving(move: Movement, controller: Player) extends State
    
  /**
   * The dice has been locked and is part of a burst group, i.e. it is either
   * charging or bursting. This state is always initiated by a player. The dice
   * occupies some space.
   */
  case class Locked(val initiator: Player, group: BurstGroup, where: Space)
    extends State
}
