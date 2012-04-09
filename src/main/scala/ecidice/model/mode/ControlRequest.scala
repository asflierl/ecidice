/*
 * Copyright (c) 2009-2012 Andreas Flierl
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

package ecidice
package model
package mode

import scalaz.Scalaz.ToValidationV

/**
 * Defines the rules for how and when a player may gain control over a die.
 * 
 * If the player is already controlling a die, control is retained on that
 * die.
 * 
 * If there's two dice at the player's location and the upper die is solid
 * and not under the control of another player, control will be granted on
 * that upper die.
 * 
 * If there's only one die at the player's location (which must be on the
 * floor level) that is solid and not under the control of any player,
 * control is granted on that die.
 * 
 * In all other cases, the control request will be rejected.
 * 
 * @author Andreas Flierl
 */
trait ControlRequest[A <: Mode[A]] extends Helpers { this: A =>
  def control(player: Player): Valid[A] = {
    def controlTile(loc: Tile): Valid[A] = 
      if (! isEmpty(board(loc.raised))) controlSpace(loc.raised)  
      else controlSpace(loc.floor)
    
    def controlSpace(space: Space): Valid[A] = board(space) match {
      case d @ Die(_,_,_) => (controlDie(space, d)).success
      case _ => ("no controllable die at " + space).failNel
    }
    
    def controlDie(space: Space, die: Die): A =
      copy(board = board + (space -> SolidControlled(die, player)),
           players = players + (player -> ControllingADie(space)))
    
    def retainControl(assignment: Assignment): A = copy(players = players + (player -> assignment))
           
    players(player) match {
      case Standing(t) => controlTile(t)
      case ControllingADie(s) => this.success
      case MovingWithADie(mov, _) => retainControl(MovingWithADie(mov, false)).success
      case FallingWithADie(fall, _) => (retainControl(FallingWithADie(fall, false))).success 
      case x => (player.toString + " may not control anything right now: " + x).failNel
    }
  }
}
