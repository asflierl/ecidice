/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model

import Level._
import Board.spacesToTiles

import collection.breakOut
import annotation.tailrec

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
final class DiceMatcher(board: Board) {
  def find(startAt: (Space, Dice)) = {
    val (startSpace, startDice) = startAt
    val top = startDice.top
    
    @tailrec
    def search(stack: List[(Space, Dice)], included: Set[Tile], group: Map[Space, Dice]): Map[Space, Dice] = stack match {
      case Nil => group
      
      case (topOfStack @ (space, Dice(`top`, _, _))) :: restOfStack => {
        val next = surroundingsOf(space, included) 
        search(next ++ restOfStack, included -- (next map spacesToTiles), group + topOfStack)
      }
      
      case x :: xs => search(xs, included, group)
    }
    
    search(List(startAt), board.tiles - startSpace.tile, Map())
  }
  
  def surroundingsOf(space: Space, included: Set[Tile]): List[(Space, Dice)] =
      Direction.values
               .view
               .map(space.tile.look)
               .filter(included.contains)
               .map(_.floor)
               .map(space => (space, board(space)))
               .collect { case (s, d: Dice) => (s, d) }
               .toList
}
object DiceMatcher {
  def apply(board: Board) = new DiceMatcher(board)
}
