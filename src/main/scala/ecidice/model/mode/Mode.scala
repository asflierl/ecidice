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

// 2-player situation: player 1 upon a charging die, die bursts, player 2
// wants to move where the die burst; it must be ensured that player 2 won't 
// move there

//TODO some kind of scoring system 
//TODO when do new dice spawn?
//TODO take tile visibility into account

/**
 * Common interface for game modes.
 */
trait Mode[A <: Mode[A]] { this: A =>
  def board: Board
  def locks: Set[DieLock[_]]
  def players: Map[Player, Assignment]
  
  def spawnPlayer(tile: Tile): Valid[A]
  def spawnDie(tile: Tile, now: Instant, die: Die = Die.random): Valid[A]
  
  def control(player: Player): Valid[A]
  def relinquish(player: Player): A
  def move(player: Player, dir: Direction, now: Instant): Valid[A]
  
  def copy(board: Board = board, 
           locks: Set[DieLock[_]] = locks,
           players: Map[Player, Assignment] = players): A
}
