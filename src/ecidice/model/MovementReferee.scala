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
 * Defines the rules for when and where a player is allowed to move.
 * <p>
 * A movement request (by a player) is granted (only) if the player controls a 
 * dice, the destination position would be on the game board, the destination is
 * either a free floor space or a free space upon an uncontrolled, solid dice.
 * 
 * @author Andreas Flierl
 */
class MovementReferee(board: Board, clock: Clock, tracker: ActivityTracker) {
  private var player : Player = _
  private var direction : Direction.Value = _
  private var dice : Dice = _
  
  /**
   * Requests for a player to move in direction <code>dir</code>.
   * <p>
   * On a successful request, this method sets all necessary model state to
   * represent the new situation.
   * 
   * @param p the player requesting to move
   * @param dir the direction the player wants to move in
   * @return whether the move was allowed (and started)
   */
  def requestMove(player: Player, direction: Direction.Value) : Boolean = {
    this.player = player
    this.direction = direction
    
    player.state match {
      case Player.Standing(tile) => requestPlayerMove(player, direction, tile)

      case Player.Controlling(dice) => {
        this.dice = dice
        requestDiceMove(player, direction)
      }
      
      /* Player wants to move to the same place she's already moving to: 
       * leave things as they are and return <code>true</code>.
       */
      case Player.Moving(_, from, to, _) 
        if (from.pos == board.positionInDir(from, direction)) 
        => true
      
      case _ => false
    }
  }
  
  /* This is the easy case: the player controls no dice and just wants to
   * move around.
   */
  private def requestPlayerMove(player: Player, direction: Direction.Value,
      start: Tile) : Boolean = {
    
    val destination = board.positionInDir(start, direction)
    if (! board.isWithinBounds(destination)) return false 

    val mov = Player.Moving(player, start, board(destination), 
                            Timespan(clock, Game.MOVE_DURATION))
    player.state = mov
    tracker.track(mov)
    true
  }

  /* Somewhat tricky: the player controls a dice and wants to move along with
   * it. This is only granted if the target position is wihin bounds and the
   * tile at that position is free to be moved to.
   */
  private def requestDiceMove(player: Player, direction: Direction.Value) : Boolean = {    
    dice.state match {
      case Dice.Solid(start, Some(controller)) if (player == controller) =>
        tryToMoveFrom(start)

      case Dice.Moving(Movement(_, from, to, _, _), controller) 
        if (wouldBeSameMovement(controller, from, to)) => true
      
      case _ => false // dice appearing, bursting or moving in another direction
    }
  }
  
  private def tryToMoveFrom(start: Space) : Boolean = {
    val position = board.positionInDir(start.tile, direction)
    
    if (! board.isWithinBounds(position)) return false
    
    val tile = board(position)
    val destination = findDestinationSpace(tile)
    
    if (destination.isEmpty) return false
    
    startDiceMovement(start, destination.get)
    true
  }
  
  private def wouldBeSameMovement(controller: Player, from: Space, to: Space) =
    (player == controller) &&
    (to.tile.pos == board.positionInDir(from.tile, direction))
  
  private def startDiceMovement(from: Space, to: Space) = {
    val when = Timespan(clock, Game.MOVE_DURATION)
    val transform = Transform(from, to, direction)
    val move = Movement(dice, from, to, when, transform)
    
    tracker.track(move)
    
    from.content = move
    to.content = move
    dice.state = Dice.Moving(move, player)
  }
  
  private def findDestinationSpace(tile: Tile) : Option[Space] = 
    tile.floor.content match {
      case Empty => Some(tile.floor)   
      case Occupied(byDiceOnFloor) => examineOccupied(byDiceOnFloor)
      case _ : Movement => None
  }
  
  private def examineOccupied(diceOnFloor: Dice) = {
    diceOnFloor.state match {   
      case Dice.Solid(occupiedSpace, controller) =>
        if (controller.isDefined) None
        else examineRaised(occupiedSpace.tile.raised) 
      case _ => None // can only move onto solid dice
    }
  }
  
  private def examineRaised(destination: Space) = destination.content match {
    case Empty => Some(destination)
    case _ => None
  }
}
