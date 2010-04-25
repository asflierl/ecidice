/*
 * Copyright (c) 2009-2010, Andreas Flierl
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
 * 
 * @author Andreas Flierl
 */
class MovementReferee(board: Board, clock: Clock, tracker: ActivityTracker) {
  private var player: Player = _
  private var direction: Direction.Value = _
  private var dice: Dice = _
  
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
  def requestMove(player: Player, direction: Direction.Value): Boolean = {
    this.player = player
    this.direction = direction
    
    if (player.isStanding) requestPlayerMove(player, direction, player.location)
    else if (player.isController) {
      this.dice = player.dice
      requestDiceMove(player, direction)
    } else if (player.isMoving) wouldBeSamePlayerMove(player.movement)
    else false
  }
  
  private def wouldBeSamePlayerMove(move: PlayerMovement) =
    (move.destination.pos == board.positionInDir(player.location, direction))
  
  /* This is the easy case: the player controls no dice and just wants to
   * move around.
   */
  private def requestPlayerMove(player: Player, direction: Direction.Value,
      start: Tile): Boolean = {
    
    val destination = board.positionInDir(start, direction)
    if (! board.isWithinBounds(destination)) return false 
    val move = Activity.on(clock).playerMovement(player, start, board(destination)) 
    player.move(move)
    tracker.track(move)
    true
  }

  /* Somewhat tricky: the player controls a dice and wants to move along with
   * it. This is only granted if the target position is within bounds and the
   * tile at that position is free to be moved to.
   */
  private def requestDiceMove(player: Player, direction: Direction.Value): Boolean =
    if (dice.isSolid && player == dice.controller) tryToMoveFrom(dice.location)
    else if (dice.isMoving) wouldBeSameDiceMove(dice.movement)
    else false
    
  private def tryToMoveFrom(start: Space): Boolean = {
    val position = board.positionInDir(start.tile, direction)

    if (! board.isWithinBounds(position)) return false
    
    val tile = board(position)
    val destination = findDestinationSpace(tile)
    
    if (destination.isEmpty) return false
    
    startDiceMovement(start, destination.get)
    true
  }
  
  private def wouldBeSameDiceMove(move: DiceMovement) =
    (player == move.controller) &&
    (move.destination.tile.pos == board.positionInDir(move.origin.tile, direction))
  
  private def startDiceMovement(origin: Space, destination: Space) = {
    val transform = Transform(origin, destination, direction)
    val move = Activity.on(clock).diceMovement(dice, origin, destination, 
                                               transform, player)
    
    tracker.track(move)
    
    origin.involve(move)
    destination.involve(move)
    dice.move(move)
  }
  
  private def findDestinationSpace(tile: Tile): Option[Space] =
    if (tile.floor.isEmpty) Some(tile.floor)
    else if (tile.floor.isOccupied) examineOccupied(tile.floor.dice)
    else None
  
  private def examineOccupied(diceOnFloor: Dice) = 
    if (! diceOnFloor.isSolid) None
    else if (diceOnFloor.isControlled) None
    else examineRaised(diceOnFloor.location.tile.raised)
  
  private def examineRaised(destination: Space) = 
    if (destination.isEmpty) Some(destination)
    else None
}
