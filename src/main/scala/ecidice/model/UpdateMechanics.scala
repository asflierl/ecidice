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

import ecidice.util.NotYetImplementedException

class UpdateMechanics(board: Board, clock: Clock, tracker: ActivityTracker) {
  private val diceMatcher = new DiceMatcher(board)
  
  clock.addReaction(update _)
  
  def update {
    val finishedActivities = tracker.activities.filter(_.time.isOver)
    var diceMoves: List[DiceMovement] = Nil
    
    finishedActivities.foreach(_ match {
      case activity: DiceAppearing => diceAppeared(activity)
      case activity: PlayerMovement => playerMovementEnded(activity) 
      case activity: DiceLock => diceLocked(activity)
      case activity: DiceMovement => diceMoves = activity:: diceMoves
    })
    
    diceMoves.foreach(diceMovementEnded(_))
    
    finishedActivities.foreach(tracker.forget(_))
  }
  
  private def diceAppeared(affected: DiceAppearing) =
    affected.dice.solidify(affected.location, None)
  
  private def diceMovementEnded(move: DiceMovement) = {
    move.dice.change(move.transform)
      
    if (move.dice.top == 1) {
      board.tiles filter (_.floor.isOccupied)
    }
        
    val group = diceMatcher.find(move.dice, move.destination.tile)    
    
    throw new NotYetImplementedException
  }
  
  private def playerMovementEnded(affected: PlayerMovement) =
    affected.player.stand(affected.destination)
  
  private def diceLocked(affected: DiceLock) =
    if (affected.group.isCharging) diceCharged(affected)
    else diceBurst(affected)
  
  private def diceCharged(affected: DiceLock) = {
    val burst = Activity.on(clock).diceLock(affected.group.cloneAsBursting)
    burst.group.dice.foreach(dice => dice.lock(burst, dice.initiator))
    tracker.track(burst)
  }
  
  private def diceBurst(affected: DiceLock) = {
    affected.group.dice.foreach(dice => {
      dice.location.empty()
      dice.burst()
    })
  }
}
