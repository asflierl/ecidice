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

import Level._

import scala.collection.breakOut

/**
 * Finds and returns all dice (including `startDice`) that show the same 
 * top face as `startDice` and that are reachable from `startDice`
 * via other such dice (by only moving up, down, left or right once or 
 * several times). Only solid dice that are uncontrolled are considered.
 * 
 * As an example consider the following 3 x 3 board:
 * <pre>
 *     X Y Z
 *   ---------
 * A | 6 4 6 |
 * B | 6 3 6 |
 * C | 6 6 3 |
 *   ---------
 * </pre>
 * 
 * Starting from CX, the find method would return AX, BX, CX and CY. Starting
 * from BY, it would only return BY. Starting from BZ, it would return AZ 
 * and BZ.
 */
class DiceMatcher(board: Board, startAt: (Space, Dice)) {
  val (startSpace, startDice) = startAt
  
  def find = searchSurroundingsOf(startSpace, Map(startAt))
  
  private def searchSurroundingsOf(s: Space, group: Map[Space, Dice]): Map[Space, Dice] =
    Direction.values
             .map(diceInDirection(s, _, Floor))
             .collect { case Some(diceThere) => diceThere }
             .foldLeft(group)((accu, next) => matchTopFaces(next, accu))
  
  private def diceInDirection(space: Space, dir: Direction.Value, level: Level.Value) = {
    val tile = space.tile.look(dir)
    
    if (! board.contains(tile)) None
    else board(tile.floor) match {
      case d : Dice => Some(tile.floor -> d)
      case _ => None
    }
  }
  
  private def matchTopFaces(pair: (Space, Dice), group: Map[Space, Dice]) = {
    val (space, dice) = pair
    
    if (dice.top == startDice.top && ! group.contains(space))
      searchSurroundingsOf(space, group + (space -> dice))
    else group
  }
}
object DiceMatcher {
  def apply(board: Board, startAt: (Space, Dice)) = new DiceMatcher(board, startAt)
}
