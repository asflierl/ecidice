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
 * - Neither the names of the copyright holders nor the names of the
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

package ecidice.modelold

/**
 * Represents an area or space on the game board, either on the ground or on a
 * raised level. Such a space can be empty, occupied by a dice or be the start
 * or destination of a dice's movement.
 * 
 * @author Andreas Flierl
 * 
 * @param tile the tile that provides this space
 * @param content what's in this space
 */
class Space(val tile: Tile) {
  private var state: State = Empty
  
  def isFloor = (this == tile.floor)  
  def isRaised = (this == tile.raised)
  
  def isEmpty = state isEmpty
  def isOccupied = state isOccupied
  def isBusy = state isBusy
  def hasBursting = state hasBursting
  
  def dice = state dice
  def movement = state movement
  def burstingDice = state bursting
  
  def empty() = 
    if (hasBursting) state = EmptyAndBursting(burstingDice)
    else state = Empty
    
  def occupy(dice: Dice) =
    if (hasBursting) state = OccupiedAndBursting(dice, burstingDice)
    else state = Occupied(dice)
    
  def involve(move: DiceMovement) =
    if (hasBursting) state = BusyAndBursting(move, burstingDice)
    else state = Busy(move)
    
  def convertToBursting() =
    if (isOccupied) state = EmptyAndBursting(dice)
    else throw new IllegalStateException("no occupying dice to convert")
  
  def forgetBursting() = state match {
    case EmptyAndBursting(_) => state = Empty
    case OccupiedAndBursting(d,_) => state = Occupied(d)
    case BusyAndBursting(m,_) => state = Busy(m)
    case _ => // NOP
  }
  
  override def toString = "Space(%d, %d, %s)".format(tile.x, tile.y,
    (if (isRaised) "raised" else "floor"))
  
  sealed trait State {
    val isEmpty = false
    val isOccupied = false
    val isBusy = false
    val hasBursting = false
    def dice: Dice = throw new IllegalStateException("space not occupied")
    def movement: DiceMovement = throw new IllegalStateException("space not busy")
    def bursting: Dice = throw new IllegalStateException("no bursting dice in space")
  }

  case object Empty extends State {
    override val isEmpty = true
  }
  
  case class EmptyAndBursting(override bursting: Dice) extends State {
    override val isEmpty = true
    override val hasBursting = true
  }
  
  case class Occupied(override dice: Dice) extends State {
    override val isOccupied = true
  }
  
  case class OccupiedAndBursting(override dice: Dice, override bursting: Dice) extends State {
    override val isOccupied = true
    override val hasBursting = true
  }
  
  case class Busy(override movement: DiceMovement) extends State {
    override val isBusy = true
  }
  
  case class BusyAndBursting(override movement: DiceMovement, override bursting: Dice) extends State {
    override val isBusy = true
    override val hasBursting = true
  }
}
