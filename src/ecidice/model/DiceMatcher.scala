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
 * Finds and returns all dice (including <code>src</code>) that show the same 
 * top face as <code>src</code> and that are reachable from <code>src</code>
 * via other such dice (by only moving up, down, left or right once or 
 * several times). The <code>src</code> dice is expected to be in the state
 * <code>Dice.Moving</code> and already have the transform associated with
 * the move applied to it. Other than that, only solid dice that are 
 * uncontrolled are considered.
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
  var src : Dice = _
  
  def find(src: Dice, start: Tile) : Set[Dice] = {
    this.src = src
    findFromTile(start, Set(src))
  }
  
  private def findFromDice(d: Dice, g: Set[Dice]) : Set[Dice] = 
    if (d.top != src.top || g.contains(d)) g
    else d.state match {
      case Dice.Solid(s, c) if (c == None) => findFromTile(s.tile, g + d)
      case _ => g
    }
  
  private def findFromTile(t: Tile, g: Set[Dice]) = {
    var res = g
    Direction.elements.foreach(
      diceInDir(t, _, Tile.Level.FLOOR).foreach( 
        (next) => res = findFromDice(next, res)))
    res
  }
  
  private def diceInDir(t: Tile, dir: Direction.Value, level: Tile.Level.Value) 
      : Option[Dice] = {
    val pos = board.positionInDir(t, dir)
    if (board.isWithinBounds(pos)) {
      val tgt = board(pos).floor
      
      tgt.content match {
        case Occupied(d) => Some(d)
        case _ => None
      }
    } else None
  }
}
