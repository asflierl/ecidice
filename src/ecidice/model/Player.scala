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

class Player(game: Game, spawnPoint: Tile) {
  var state : Player.State = Player.Standing(spawnPoint)
  
  def controlledDice = state match {
    case Player.Controlling(d) => Some(d)
    case _ => None
  }
  
  /**
   * Requests for this player to gain control over the dice below her.
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
   * <p>
   * On a successful request, this method sets all necessary model state to
   * represent the new situation.
   * 
   * @param p the player requesting control over a dice
   * @return the dice that is under the control of the player or 
   *         <code>None</code>
   */
  def requestControl() : Option[Dice] = state match {
    case Player.Standing(t) => {
      t.requestControl(this).map { dice =>
        state = Player.Controlling(dice)
        dice
      }
    }
    case Player.Controlling(d) => Some(d)
    case _ => None
  }
  
  /**
   * Requests for player <code>p</code> to move in direction <code>dir</code>.
   * <p>
   * If the player is not controlling a dice and the position after the move
   * would be still on the game board, the request is granted.
   * <p>
   * If the player is controlling a dice, the dice is "solid", the destination
   * is within board bounds and the destination tile has an empty space on top,
   * the request is granted.
   * <p>
   * If the player is already moving to the same position (with or without 
   * dice), the request is granted.
   * <p>
   * In all other cases, the movement request will be rejected.
   * <p>
   * On a successful request, this method sets all necessary model state to
   * represent the new situation.
   * 
   * @param p the player requesting to move
   * @param dir the direction the player wants to move in
   * @return whether the move was allowed (and started)
   */
  def requestMove(dir: Direction.Value) : Boolean = state match {
    /* This is the easy case: the player controls no dice and just wants to move
     * around.
     */
    case Player.Standing(t @ Tile(x, y, _)) => {
      val pos = t.board.positionInDir(t, dir)
      
      if (t.board.isWithinBounds(pos)) {
        val mov = Player.Moving(this, t, t.board(pos), 
                                game.clock.createTimespanWithLength(Game.MOVE_DURATION))
        state = mov
        game.tracker.track(mov)
        true
      } else false // destination out of board bounds
    }
    
    /* Somewhat tricky: the player controls a dice and wants to move along with
     * it. This is only granted if the target position is wihin bounds and the
     * tile at that position is free to be moved to.
     */
    case Player.Controlling(d) => game.diceMovementResolver.requestMove(this, dir)
    
    /* Player wants to move to the same place she's already moving to: 
     * leave things as they are and return <code>true</code>.
     */
    case Player.Moving(_, from, to, _) 
      if (from.pos == to.board.positionInDir(from, dir)) 
      => true
    
    case _ => false
  }
}
object Player {
  sealed abstract class State
  
  case class Standing(where: Tile) extends State
  
  case class Controlling(dice: Dice) extends State
  
  case class Moving(player: Player, from: Tile, to: Tile, when: Timespan)
    extends State with Timed
}