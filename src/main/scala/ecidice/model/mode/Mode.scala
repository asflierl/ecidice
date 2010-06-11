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

//TODO it probably should be possible to move onto charging dice!
// 2-player race condition: player 1 upon a charging dice, dice bursts, player 2
// wants to move where the dice burst
//TODO falling dice must be modeled (probably linked to burst time?)
//TODO it also should probably be possible to move onto an appearing dice

//TODO some kind of scoring system 
//TODO when do new dice spawn?
//TODO take tile visibility into account

/**
 * Common interface for game modes.
 */
trait Mode[A <: Mode[A]] { this: A =>
  def board: Board
  def locks: Set[DiceLock[_]]
  def players: Map[Player, Assignment]
  
  def spawnPlayer(tile: Tile): A
  def spawnDice(tile: Tile, now: Instant, dice: Dice = Dice.random): A
  
  def control(player: Player): A
  def relinquish(player: Player): A
  def move(player: Player, dir: Direction.Value): A
  
  def dupe(board: Board = board, 
           locks: Set[DiceLock[_]] = locks,
           players: Map[Player, Assignment] = players): A
}
