package ecidice.model

import Level._

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
  
  def find = findFromSpace(startSpace, Set(startAt))
  
  private def findFromSpace(s: Space, g: Set[(Space, Dice)]): Set[(Space, Dice)] = {
    var res = g
    Direction.values.foreach(
      diceInDir(s, _, Floor).foreach( 
        (next) => res = findFromSpaceAndDice(next, res)))
    res
  }
  
  private def diceInDir(s: Space, dir: Direction.Value, level: Level.Value) = {
    val pos = s.tile.look(dir)
    if (board.contains(pos)) board(pos.floor) match {
      case d : Dice => Some(pos.floor -> d)
      case _ => None
    } else None
  }
  
  private def findFromSpaceAndDice(pos: (Space, Dice), group: Set[(Space, Dice)]) = {
    val (space, dice) = pos
    
    if (dice.top == startDice.top && ! group.contains(space -> dice))
      findFromSpace(space, group + (space -> dice))
    else group
  }
}
object DiceMatcher {
  def apply(board: Board, startAt: (Space, Dice)) = new DiceMatcher(board, startAt)
}