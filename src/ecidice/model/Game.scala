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

import scala.collection.immutable._

/**
 * Central mediator that manages the objects participating in a game and updates
 * their state according to requested actions within the game rules.
 * 
 * @author Andreas Flierl
 */
//TODO it probably should be possible to move onto charging dice!
//TODO bursting dice should be ethereal
//TODO falling dice must be modeled (probably linked to burst time?)
//TODO relinquish control must be modeled
//TODO some kind of scoring system 
class Game(numPlayers: Int, val board: Board) {
  lazy val players = createPlayers(0)
  
  val clock = new Clock() {
    def update(elapsedTime: Double) = addToCurrentTime(elapsedTime)
  }
  
  val tracker = new ActivityTracker
  
  val diceMovementResolver = new DiceMovementResolver(board, clock, tracker)
  
  /**
   * Creates <code>num</code> players in this game, starting at the board's 
   * predefined spawn locations.
   */
  private def createPlayers(num: Int) : List[Player] =
    if (num == numPlayers) Nil
    else new Player(this, board.spawnPoints(num)) :: createPlayers(num + 1)
  
  /**
   * Updates this game after the specified amount of time has elapsed.
   * 
   * @param elapsed the elapsed time (in seconds as float)
   */
  def update(elapsed: Float) {
    clock.update(elapsed)
    
    var stuffToRemove : List[Timed] = Nil
    var moves : List[Movement] = Nil
    
    // process timed stuff that is over
    tracker.activities.foreach((x) => if (x.when.isOver) {
      stuffToRemove = x :: stuffToRemove
      
      x match {
        case da : Dice.Appearing => diceAppeared(da)
        case m : Movement => moves = m :: moves // process those later
        case pm : Player.Moving => playerMovementEnded(pm) 
        case bg : BurstGroup => burstGroupTimedOut(bg)
      }
    })
    
    moves.foreach((m) => {
      
    })
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
        
    val group = find(m.dice, m.to.tile)    
    
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
  
  /**
   * Finds and returns all dice (including <code>src</code>) that show the same 
   * top face as <code>src</code> and that are reachable from <code>src</code>
   * via other such dice (by only moving up, down, left or right once or 
   * several times). The <code>src</code> dice is expected to be in the state
   * <code>Dice.Moving</code> and already have the transform associated with
   * the move applied to it. Other than that, only solid dice that are 
   * uncontrolled are considered.
   * <p>
   * As an example consider the following 3 x 3 board:
   * <pre>
   *     X Y Z
   *   ---------
   * A | 6 4 6 |
   * B | 6 3 6 |
   * C | 6 6 3 |
   *   ---------
   * </pre>
   * 
   * Starting from CX, this method would return AX, BX, CX and CY. Starting from
   * BY, it would only return BY. Starting from BZ, it would return AZ and BZ.
   */
  private[model] def find(src: Dice, start: Tile) : Set[Dice] = {
    def findFromDice(d: Dice, g: Set[Dice]) : Set[Dice] = 
      if (d.top != src.top || g.contains(d)) g
      else d.state match {
        case Dice.Solid(s, c) if (c == None) => findFromTile(s.tile, g + d)
        case _ => g
      }
    
    def findFromTile(t: Tile, g: Set[Dice]) = {
      var res = g
      Direction.elements.foreach(
        board.diceInDir(t, _, Tile.Level.FLOOR).foreach( 
          (next) => res = findFromDice(next, res)))
      res
    }
    
    findFromTile(start, Set(src))
  }
  
  /**
   * Spawns a dice at the specified position on the board and updates the
   * state in the participating components accordingly.
   * 
   * @param x the horizontal position on the board
   * @param y the depth position on the board
   * @return optionally the new dice
   */
  def spawnDice(x: Int, y: Int) = board(x, y).floor.content match {
    case Empty => {
      val d = new Dice
      board(x, y).floor.content = Occupied(d)
      val app = Dice.Appearing(board(x, y).floor, 
                               clock.createTimespanWithLength(Game.APPEAR_DURATION))
      d.state = app
      tracker.track(app)
      Some(d)
    }
    case _ => None
  }
}
object Game {
  val MOVE_DURATION = 0.25f
  val APPEAR_DURATION = 5f
  val CHARGE_DURATION = 10f
  val BURST_DURATION = 1f  
}
