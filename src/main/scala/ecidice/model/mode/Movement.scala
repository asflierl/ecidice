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

import time._
import scalaz.Scalaz.ToValidationV

/**
 * Defines the (partially quite complicated) rules for movement.
 * 
 * Players may move alone, which is rather simple: a player can move to any
 * tile on the game board. If she's already moving, movement continues.
 */
trait Movement[A <: Mode[A]] { this: A =>

  def move(player: Player, dir: Direction, now: Instant): Valid[A] = players(player) match {
    case Standing(tile) => 
      movePlayer(PlayerMovement(player, tile, tile.look(dir), now))
      
    case ControllingADie(origin) => moveDie(player, origin, dir, now)
    
    case MovingAlone(_) | MovingWithADie(_, _) => this.success
    
    case _ => (player.toString + " may not move " + dir).failNel
  }
  
  private def movePlayer(move: PlayerMovement): Valid[A] =
    if (board.contains(move.destination))
      copy(players = players + (move.player -> MovingAlone(move))).success
    else (move.destination.toString + " outside of " + board).failNel
  
  private def moveDie(player: Player, origin: Space, dir: Direction, now: Instant): Valid[A] = {
    val destinationTile = origin.tile.look(dir)
    
    def decideLevel = board(destinationTile.floor) match {
      case Empty => moveTo(destinationTile.floor).success
      
      case Die(_, _, _) | DieAppearing(_, _, _) | Charging => 
        moveTo(destinationTile.raised).success
        
      case _ => (destinationTile.floor.toString + " does not allow movement onto the tile").failNel
    }

    def moveTo(destination: Space) = {
      val move = DieMovement(dieAt(origin), origin, destination,
                             Transform(origin, destination, dir), player, now)
                              
      copy(players = players + (player -> MovingWithADie(move, false)),
           board   = board   + (origin -> move) + (destination -> move))
    }
    
    if (isEmpty(board(destinationTile.raised))) decideLevel
    else (destinationTile.raised.toString + " is not empty").failNel
  }
  
  private def dieAt(loc: Space) = board(loc) match { 
    case SolidControlled(die, _) => die
  }
}
