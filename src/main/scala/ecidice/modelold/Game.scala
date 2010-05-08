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

package ecidice.modelold

import scala.collection.immutable._

/**
 * Central mediator that manages the objects participating in a game and updates
 * their state according to requested actions within the game rules.
 * 
 * @author Andreas Flierl
 */
class Game(numPlayers: Int, val board: Board) {
  lazy val players = createPlayers(0)
  val clock = new Clock
  val tracker = new ActivityTracker
  val updateMechanics = new UpdateMechanics(board, clock, tracker)
  val movementReferee = new MovementReferee(board, clock, tracker)
  val controlReferee = new ControlReferee
  
  /**
   * Creates <code>num</code> players in this game, starting at the board's 
   * predefined spawn locations.
   */
  private def createPlayers(num: Int): List[Player] =
    if (num == numPlayers) Nil
    else new Player(board.spawnPoints(num)) :: createPlayers(num + 1)
  
  /**
   * Spawns a dice at the specified position on the board and updates the
   * state in the participating components accordingly.
   * 
   * @param x the horizontal position on the board
   * @param y the depth position on the board
   * @return optionally the new dice
   */
  def spawnDice(x: Int, y: Int) = {
    val space = board(x, y).floor
    
    if (space.isEmpty) {
      val dice = new Dice
      space.occupy(dice)
      val activity = Activity.on(clock).diceAppearing(dice, space)
      dice.appear(activity)
      tracker.track(activity)
      Some(dice)
    } else None
  }
}
