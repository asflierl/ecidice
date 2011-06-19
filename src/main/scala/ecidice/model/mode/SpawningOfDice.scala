/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package mode

import time._
import Level._

/**
 * Defines the rules for spawning a new dice.
 * 
 * A new dice is only spawned if and only if both spaces at a given tile are
 * empty. The new dice always appears on the floor.
 * 
 * @author Andreas Flierl
 */
trait SpawningOfDice[A <: Mode[A]] extends Helpers { this: A =>
  def spawnDice(tile: Tile, now: Instant, dice: Dice = Dice.random) = {
    val free = Level.values.forall(l => isEmpty(board(Space(tile, l)))) 
    
    if (free) {
      val space = Space(tile, Floor)
      val activity = DiceAppearing(Dice.default, space, now)
      dupe(board = board + (space -> activity))
    } else this
  }
}
