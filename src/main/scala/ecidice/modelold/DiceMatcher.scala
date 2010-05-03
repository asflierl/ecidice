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

/**
 * Finds and returns all dice (including <code>src</code>) that show the same 
 * top face as <code>src</code> and that are reachable from <code>src</code>
 * via other such dice (by only moving up, down, left or right once or 
 * several times). Only solid dice that are uncontrolled are considered.
 * <p>
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
class DiceMatcher(board: Board) {
  var src: Dice = _
  
  def find(src: Dice, start: Tile): Set[Dice] = {
    this.src = src
    findFromTile(start, Set(src))
  }
  
  private def findFromTile(t: Tile, g: Set[Dice]) = {
    var res = g
    Direction.values.foreach(
      diceInDir(t, _, Tile.Level.FLOOR).foreach( 
        (next) => res = findFromDice(next, res)))
    res
  }
  
  private def findFromDice(dice: Dice, group: Set[Dice]): Set[Dice] =
    if (dice.top != src.top || group.contains(dice)) group
    else if (dice.isSolid && !dice.isControlled)
      findFromTile(dice.location.tile, group + dice)
    else group
  
  private def diceInDir(t: Tile, dir: Direction.Value, level: Tile.Level.Value) = {
    val pos = board.positionInDir(t, dir)
    if (board.isWithinBounds(pos) && board(pos).floor.isOccupied)
      Some(board(pos).floor.dice)
    else None
  }
}
