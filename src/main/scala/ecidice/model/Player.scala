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

class Player(spawnPoint: Tile) {
  private var state: State = Standing(spawnPoint)
  
  def isStanding = state.isInstanceOf[Standing]
  def isController = state.isInstanceOf[Controlling]
  def isMoving = state.isInstanceOf[Moving]
  
  def location = state match {
    case Standing(somewhere) => somewhere
    case Controlling(dice) => dice.location.tile
    case _ => throw new IllegalStateException("player location undetermined")
  }
  
  def dice = state match {
    case Controlling(dice) => dice
    case _ => throw new IllegalStateException("player not controlling a dice")
  }
  
  def movement = state match {
    case Moving(activity) => activity
    case _ => throw new IllegalStateException("player not moving")
  }
  
  def stand(location: Tile) = (state = Standing(location))
  def control(dice: Dice) = (state = Controlling(dice))
  def move(move: PlayerMovement) = (state = Moving(move))
  
  sealed abstract class State
  case class Standing(where: Tile) extends State
  case class Controlling(dice: Dice) extends State
  case class Moving(activity: PlayerMovement) extends State
}
