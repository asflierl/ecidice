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
package mode

/**
 * Defines the rules for how and when a player may gain control over a dice.
 * 
 * If the player is already controlling a dice, control is retained on that
 * dice.
 * 
 * If there's two dice at the player's location and the upper dice is solid
 * and not under the control of another player, control will be granted on
 * that upper dice.
 * 
 * If there's only one dice at the player's location (which must be on the
 * floor level) that is solid and not under the control of any player,
 * control is granted on that dice.
 * 
 * In all other cases, the control request will be rejected.
 * 
 * @author Andreas Flierl
 */
trait ControlRequest[A <: Mode[A]] extends Helpers { this: A =>
  def control(player: Player) = {
    def controlTile(loc: Tile) = 
      if (! isEmpty(board(loc.raised))) controlSpace(loc.raised)  
      else controlSpace(loc.floor)
    
    def controlSpace(space: Space) = board(space) match {
      case d : Dice => createResult(space, d)
      case _ => this
    }
    
    def createResult(space: Space, dice: Dice) =
      dupe(board = board.put(space -> SolidControlled(dice, player)),
           players = players + (player -> ControllingADice(space)))
    
    def retainControl(mov: DiceMovement) =
      dupe(players = players + (player -> MovingWithADice(mov, true)))
           
    players(player) match {
      case MovingWithADice(mov, _) => retainControl(mov)   
      case Standing(t) => controlTile(t)
      case _ => this
    }
  }
}
