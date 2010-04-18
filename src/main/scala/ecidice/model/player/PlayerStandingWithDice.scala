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

package ecidice.model.player

import ecidice.model._
import ecidice.model.activity._
import ecidice.model.dice._
import ecidice.model.space._

class PlayerStandingWithDice private[player] (
    diceByName: => SolidControlledDice,
    val id: Int
) extends Player {
  lazy val dice = diceByName
  
  def move(destination: EmptySpace, transform: Transform.Value, now: Instant) = {
    lazy val player = new PlayerMovingWithDice(activity, id)
    lazy val movingDice = dice.move(activity) 
    lazy val activity: DiceMovement = DiceMovement(movingDice, player, origin, 
        destSpace, transform, now)
    lazy val origin: BusySpace = new BusySpace(dice.location.tile, activity)
    lazy val destSpace: BusySpace = new BusySpace(destination.tile, activity)
        
    player
  }
  
  def relinquishControl = 
    (new StandingPlayer(dice.location.tile, id), dice.makeUncontrolled)
}
