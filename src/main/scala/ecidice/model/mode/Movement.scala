/*
 * @author Andreas Flierl
 * Read "License.txt" in the root directory of this project for licensing details.
 */

package ecidice
package model
package mode

import time._

/**
 * Defines the (partially quite complex) rules for movement.
 * 
 * Players may move alone, which is rather simple: a player can move to any
 * tile on the game board. If she's already moving, movement continues.
 */
trait Movement[A <: Mode[A]] extends Helpers { this: A =>

  def move(player: Player, dir: Direction.Value, now: Instant) = players(player) match {
    case Standing(tile) => 
      movePlayer(PlayerMovement(player, tile, tile.look(dir), now))
      
    case ControllingADice(origin) => moveDice(player, origin, dir, now)
    
    case _ => this
  }
  
  private def movePlayer(move: PlayerMovement) = {
    if (board.contains(move.destination))
      dupe(players = players + (move.player -> MovingAlone(move)))
    else this
  }
  
  private def moveDice(player: Player, origin: Space, dir: Direction.Value, now: Instant) = {
    val destinationTile = origin.tile.look(dir)
    
    def decideLevel = board(destinationTile.floor) match {
      case Empty => moveTo(destinationTile.floor)
      
      case Dice(_, _, _) | DiceAppearing(_, _, _) | Charging => 
        moveTo(destinationTile.raised)
        
      case _ => this
    }

    def moveTo(destination: Space) = {
      val move = DiceMovement(diceAt(origin), origin, destination,
                              Transform(origin, destination, dir), player, now)
                              
      dupe(players = players + (player -> MovingWithADice(move, false)),
           board   = board   + (origin -> move) + (destination -> move))
    }
    
    if (isEmpty(board(destinationTile.raised))) decideLevel
    else this
  }
  
  private def diceAt(loc: Space) = board(loc) match { 
    case SolidControlled(dice, _) => dice
  }
}
