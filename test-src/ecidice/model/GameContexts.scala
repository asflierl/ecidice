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

import org.specs.Specification
import org.specs.specification.Context

trait GameContexts extends Specification {
  var board : Board = _
  var game : Game = _
  
  val simpleGame = beforeContext {
    board = new Board(3, 3)
    game = new Game(2, board)
  }
  
  def p1 = game.players(0)
  def p2 = game.players(1)
  
  def placePlayer(player: Player, pos: (Int, Int)) : Unit =
      player.state = Player.Standing(board(pos))
  
  def within(ctx: Context)(action: => Any) = {
    ctx.beforeActions()
    action
    ctx.afterActions()
  }
  
  /**
   * Helper method that creates a new dice and places it at the topmost 
   * available space at the specified position or throws an exception if this
   * fails.
   * 
   * @param x the horizontal component of the position to place the dice
   * @param y the depth component of the position to place the dice
   * @return the newly created and placed dice
   */
  def placeDice(pos: (Int, Int)) : Dice = {
    val destinationTile = board(pos)
    val destinationSpace = destinationTile.floor.content match {
      case Empty => destinationTile.floor
      case Occupied(_) => destinationTile.raised
      case _ => throw new IllegalStateException("tile not in a state for a"
                                                + " dice to be placed")
    }
    
    val dice = new Dice
    destinationSpace.content = Occupied(dice)
    dice.state = Dice.Solid(destinationSpace, None)
    dice
  }
}