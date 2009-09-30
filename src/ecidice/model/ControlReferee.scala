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
 * Defines the rules for how and when a player may gain control over a dice.
 * <p>
 * If the player is already controlling a dice, control is retained on that
 * dice.
 * <p>
 * If there's two dice at the player's location and the upper dice is solid
 * and not under the control of another player, control will be granted on
 * that upper dice.
 * <p>
 * If there's only one dice at the player's location (which must be on the
 * floor level) that is solid and not under the control of any player,
 * control will be granted on that dice.
 * <p>
 * In all other cases, the control request will be rejected.
 * 
 * @author Andreas Flierl
 */
class ControlReferee {
  private var player : Player = _
  
  /**
   * Requests for this player to gain control over the dice below her.
   * <p>
   * On a successful request, this method sets all necessary model state to
   * represent the new situation.
   * 
   * @param p the player requesting control over a dice
   * @return the dice that is under the control of the player now or 
   *         <code>None</code>
   */
  def requestControl(player: Player) : Option[Dice] = {
    this.player = player
    player.state match {
      case Player.Standing(tile) => requestControlOnTile(tile)
        
      case Player.Controlling(dice) => Some(dice)
      
      case _ => None
    }
  }
    
  private def requestControlOnTile(tile: Tile) = {
    if (tile.raised.isEmpty) requestControlInSpace(tile.floor)
    else requestControlInSpace(tile.raised) 
  }
  
  private def requestControlInSpace(space: Space) = {
    space.content match {
      case Occupied(dice) => requestControlOverDice(dice)
      case _ => None
    }
  }
  
  private def requestControlOverDice(dice: Dice) = dice.state match {
    case Dice.Solid(space, controller) => 
      if (controller.isEmpty) {
        dice.state = Dice.Solid(space, Some(player))
        player.state = Player.Controlling(dice)
        Some(dice) 
      } else None
    case _ => None
  }
}
