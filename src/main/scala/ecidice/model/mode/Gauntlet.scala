/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package mode

/**
 * Game mode "gauntlet": players have to keep the board free against dice
 * spawning. After a certain amount of points gained the level (or difficulty)
 * increases which increases the spawn rate of dice.
 */
case class Gauntlet(
  board: Board,
  locks: Set[DiceLock[_]],
  players: Map[Player, Assignment]
) extends Mode[Gauntlet]
     with SpawningOfDice[Gauntlet]
     with SpawningOfPlayer[Gauntlet]
     with ControlRequest[Gauntlet]
     with RelinquishRequest[Gauntlet]
     with Movement[Gauntlet] {
  
  def dupe(board: Board, locks: Set[DiceLock[_]], players: Map[Player, Assignment]) = 
    Gauntlet(board, locks, players)
}
object Gauntlet {
  def create(d: Int) = Gauntlet(Board.sized(d, d), Set.empty, Map.empty)
}
