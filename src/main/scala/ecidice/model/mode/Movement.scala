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

import time._

/**
 * Defines the (partially quite complex) rules for movement.
 * 
 * Players may move alone, which is rather simple: a player can move to any
 * tile on the game board. If she's already moving, movement continues.
 */
trait Movement[A <: Mode[A]] extends Helpers { this: A =>
  def move(player: Player, dir: Direction.Value, now: Instant) = players(player) match {
    case Standing(tile) => 
      movePlayer(PlayerMovement(player, tile, tile.look(dir), now))
    case ControllingADice(origin) => moveDice(player, origin, dir, now)
    case _ => this
  }
  
  private def movePlayer(move: PlayerMovement) = {
    if (board.contains(move.destination))
      dupe(players = players + (move.player -> MovingAlone(move)))
    else this
  }
  
  private def moveDice(player: Player, origin: Space, dir: Direction.Value, now: Instant) = {
    val destinationTile = origin.tile.look(dir)
    
    def decideLevel = board(destinationTile.floor) match {
      case Empty => moveTo(destinationTile.floor)
      case Dice(_, _, _) | DiceAppearing(_, _, _) => moveTo(destinationTile.raised)
      case _ => this
    }

    def moveTo(destination: Space) = {
      val move = DiceMovement(diceAt(origin), origin, destination,
                              Transform(origin, destination, dir), player, now)
                              
      dupe(players = players + (player -> MovingWithADice(move, false)),
           board = board + (origin -> move) + (destination -> move))
    }
    
    if (isEmpty(board(destinationTile.raised))) decideLevel
    else this
  }
  
  private def diceAt(loc: Space) = board(loc) match { 
    case SolidControlled(dice, _) => dice
  }
}
