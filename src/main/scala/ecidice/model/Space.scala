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

package ecidice.model

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
  
  def isEmpty = state match {
    case Empty => true
    case EmptyAndBursting(_) => true
    case _ => false
  }
  
  def isOccupied = state match {
    case Occupied(_) => true
    case OccupiedAndBursting(_,_) => true
    case _ => false
  }
  
  def isBusy = state match {
    case Busy(_) => true
    case BusyAndBursting(_,_) => true
    case _ => false
  }
  
  def hasBursting = state match {
    case EmptyAndBursting(_) => true
    case OccupiedAndBursting(_,_) => true
    case BusyAndBursting(_,_) => true
    case _ => false
  }
  
  def dice = state match {
    case Occupied(dice) => dice
    case OccupiedAndBursting(dice,_) => dice
    case _ => throw new IllegalStateException("space not occupied")
  }
  
  def movement = state match {
    case Busy(movement) => movement
    case BusyAndBursting(movement,_) => movement
    case _ => throw new IllegalStateException("space not busy")
  }
  
  def burstingDice = state match {
    case EmptyAndBursting(d) => d
    case OccupiedAndBursting(_,d) => d
    case BusyAndBursting(_,d) => d
    case _ => throw new IllegalStateException("no bursting dice in space")
  }
  
  def empty() = 
    if (hasBursting) state = EmptyAndBursting(burstingDice)
    else state = Empty
    
  def occupy(dice: Dice) =
    if (hasBursting) state = OccupiedAndBursting(dice, burstingDice)
    else state = Occupied(dice)
    
  def involve(move: DiceMovement) =
    if (hasBursting) state = BusyAndBursting(move, burstingDice)
    else state = Busy(move)
    
  def convertToBursting() = state match {
    case Occupied(dice) => state = EmptyAndBursting(dice)
    case OccupiedAndBursting(dice, _) => state = EmptyAndBursting(dice)
    case _ => throw new IllegalStateException("no occupying dice to convert")
  }
  
  def forgetBursting() = state match {
    case EmptyAndBursting(_) => state = Empty
    case OccupiedAndBursting(d,_) => state = Occupied(d)
    case BusyAndBursting(m,_) => state = Busy(m)
    case _ => // NOP
  }
  
  override def toString = "Space(%d, %d, %s)".format(tile.x, tile.y,
    (if (isRaised) "raised" else "floor"))
  
  sealed trait State

  /**
   * Denotes that a space is empty. A dice can move or appear here (if this is
   * on the floor level).
   */
  case object Empty extends State 
  
  /**
   * Denotes that a space is empty, a dice can move or appear here (if this is
   * on the floor level), but there's still a dice burst going on at this space.
   */
  case class EmptyAndBursting(bursting: Dice) extends State
  
  /**
   * This marks a space as occupied (by a dice). Other dice can not move or
   * appear here.
   */
  case class Occupied(dice: Dice) extends State
  
  /**
   * A space in this state holds a dice that occupies it (just like the Occupied
   * state) and a dice that is currently bursting.
   */
  case class OccupiedAndBursting(dice: Dice, bursting: Dice) extends State
  
  /**
   * An instance of this class is present on the "from" and "to" spaces that are
   * involved in a dice's movement during the movement.
   */
  case class Busy(activity: DiceMovement) extends State
  
  /**
   * A space in this state is involved in dice movemnt (just like the Busy 
   * state) and a dice that is currently bursting.
   */
  case class BusyAndBursting(activity: DiceMovement, bursting: Dice) extends State
}
