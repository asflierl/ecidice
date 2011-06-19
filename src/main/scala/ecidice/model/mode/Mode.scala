/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package mode

import time._

// 2-player situation: player 1 upon a charging dice, dice bursts, player 2
// wants to move where the dice burst; it must be ensured that player 2 won't 
// move there

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
  def move(player: Player, dir: Direction.Value, now: Instant): A
  
  def dupe(board: Board = board, 
           locks: Set[DiceLock[_]] = locks,
           players: Map[Player, Assignment] = players): A
}
