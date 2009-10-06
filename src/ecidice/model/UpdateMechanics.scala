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

class UpdateMechanics(tracker: ActivityTracker, board: Board) {
  private val diceMatcher = new DiceMatcher(board)
  
  def update {
    val finishedActivities = tracker.activities.filter(_.when.isOver)
    var diceMoves : List[Movement] = Nil
    
    finishedActivities.foreach(_ match {
      case da : Dice.Appearing => diceAppeared(da)
      case pm : Player.Moving => playerMovementEnded(pm) 
      case bg : BurstGroup => burstGroupTimedOut(bg)
      case m : Movement => diceMoves = m :: diceMoves
    })
    
    diceMoves.foreach((m) => {
      
    })
    
    finishedActivities.foreach(tracker.forget(_))
  }
  
  private def diceAppeared(da : Dice.Appearing) = {
    val s = da.where
    //TODO is the board full now?
    s.content match { 
      case Occupied(d) => d.state = Dice.Solid(s, None) 
      case _ => throw new IllegalStateException("space unoccupied")
    }
  }
  
  private def diceMovementEnded(m : Movement) = {
    m.dice.change(m.transform)
      
    if (m.dice.top == 1) {
      board.tiles filter (_.floor.content.isInstanceOf[Occupied])
    }
        
    val group = diceMatcher.find(m.dice, m.to.tile)    
    
    Nil //TODO
  }
  
  private def playerMovementEnded(pm : Player.Moving) = {
    pm.player.state = Player.Standing(pm.to)
  }
  
  private def burstGroupTimedOut(bg : BurstGroup) = {
    bg.state match {
      case BurstGroup.Charging => {
        bg.state = BurstGroup.Bursting
        bg.when.lengthen(Game.BURST_DURATION)
      } 
      case BurstGroup.Bursting => {
        bg.dice.foreach((d) => {
          //TODO give the initiator some points
          d.state match {
            case Dice.Locked(_, g, s) if (bg == g) => s.content = Empty
            case _ => throw new IllegalStateException("dice not locked")
          }
          d.state = Dice.Burst
        })
      }
    }
  }
}
