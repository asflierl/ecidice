/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package mode

/**
 * A player that has control over a dice can relinquish control over that dice.
 * If the player is currently moving with a dice, the control will be 
 * relinquished after the move finishes.
 */
trait RelinquishRequest[A <: Mode[A]] { this: A =>
  def relinquish(player: Player) = players(player) match {
    case ControllingADice(somewhere) => relinquishSolidDice(player, somewhere)
    case move @ MovingWithADice(_, _) => relinquishMovement(player, move)
    case _ => this
  }
  
  private def relinquishSolidDice(player: Player, space: Space) = {
    val dice = board(space) match { case SolidControlled(d, _) => d }
    dupe(board = board + (space -> dice),
         players = players + (player -> Standing(space.tile)))
  }
  
  private def relinquishMovement(player: Player, move: MovingWithADice) =
    dupe(players = players + (player -> MovingWithADice(move.activity, true)))
}
