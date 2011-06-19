/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package mode

/**
 * Defines the rules for how and when a player may gain control over a dice.
 * 
 * If the player is already controlling a dice, control is retained on that
 * dice.
 * 
 * If there's two dice at the player's location and the upper dice is solid
 * and not under the control of another player, control will be granted on
 * that upper dice.
 * 
 * If there's only one dice at the player's location (which must be on the
 * floor level) that is solid and not under the control of any player,
 * control is granted on that dice.
 * 
 * In all other cases, the control request will be rejected.
 * 
 * @author Andreas Flierl
 */
trait ControlRequest[A <: Mode[A]] extends Helpers { this: A =>
  def control(player: Player) = {
    def controlTile(loc: Tile) = 
      if (! isEmpty(board(loc.raised))) controlSpace(loc.raised)  
      else controlSpace(loc.floor)
    
    def controlSpace(space: Space) = board(space) match {
      case d : Dice => controlDice(space, d)
      case _ => this
    }
    
    def controlDice(space: Space, dice: Dice) =
      dupe(board = board + (space -> SolidControlled(dice, player)),
           players = players + (player -> ControllingADice(space)))
    
    def retainControl(mov: DiceMovement) =
      dupe(players = players + (player -> MovingWithADice(mov, true)))
           
    players(player) match {
      case MovingWithADice(mov, _) => retainControl(mov)   
      case Standing(t) => controlTile(t)
      case _ => this
    }
  }
}
