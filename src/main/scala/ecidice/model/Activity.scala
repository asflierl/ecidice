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

package ecidice.model

/**
 * Indicates that a game event is timed and needs to be tracked/managed.
 * 
 * @author Andreas Flierl
 */
sealed abstract class Activity {
  def time : Timespan
}

object Activity {
  val MOVE_DURATION = 0.25f
  val APPEAR_DURATION = 5f
  val CHARGE_DURATION = 10f
  val BURST_DURATION = 1f 
  
  def on(clock: Clock) = {
    object Factory {
      def diceAppearing(dice: Dice, location: Space) =
        DiceAppearing(dice, location, Timespan(clock, APPEAR_DURATION))
      
      def diceMovement(dice: Dice, origin: Space, destination: Space, 
          transform: Transform.Value, controller: Player) =
        DiceMovement(dice, origin, destination, transform, controller,
                     Timespan(clock, MOVE_DURATION))
      
      def diceLock(group: DiceGroup) =
        DiceLock(group, Timespan(clock, CHARGE_DURATION))
      
      def playerMovement(player: Player, origin: Tile, destination: Tile) =
        PlayerMovement(player, origin, destination, 
                       Timespan(clock, MOVE_DURATION))
    }
    Factory
  }
}

case class DiceAppearing(dice: Dice, location: Space, time: Timespan)
  extends Activity
  
case class DiceMovement(dice: Dice, origin: Space, destination: Space, 
                        transform: Transform.Value, controller: Player,
                        time: Timespan) extends Activity
                        
case class DiceLock(group: DiceGroup, time: Timespan) extends Activity

case class PlayerMovement(player: Player, origin: Tile, destination: Tile,
                          time: Timespan) extends Activity