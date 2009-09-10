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
class Game(numPlayers: Int, board: Board) {
  val MOVE_DURATION = 0.25f
  val APPEAR_DURATION = 5f
  val CHARGE_DURATION = 10f
  val BURST_DURATION = 1f
  
  lazy val players = createPlayers(0)
  
  private var currentTime = 0f
  private var timedStuff : List[Timed] = Nil
  
  /**
   * Creates <code>num</code> players in this game, starting at the board's 
   * predefined spawn locations.
   */
  private def createPlayers(num: Int) : List[Player] =
    if (num == numPlayers) Nil
    else new Player(this, board.spawnPoints(num)) :: createPlayers(num + 1)
  
  def now = currentTime
  
  def nowFor(someTime: Float) = new Timespan(this, now, someTime)
  
  /**
   * Updates this game after the specified amount of time has elapsed.
   * 
   * @param elapsed the elapsed time (in seconds as float)
   */
  def update(elapsed: Float) {
    currentTime += elapsed
    
    var stuffToRemove : List[Timed] = Nil
    var moves : List[Movement] = Nil
    
    // process timed stuff that is over
    timedStuff.foreach((x) => if (x.when.isOver) {
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
        bg.when.lengthen(BURST_DURATION)
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
   * Requests for player <code>p</code> to gain control over the dice below her.
   * <p>
   * If the player is already controlling a dice, control is retained on that
   * dice.
   * <p>
   * If there's two dice at the player's location and the upper dice is solid
   * and not under the control of another player, control will be granted on
   * that upper dice.
   * <p>
   * If there's only one dice at the player's location (which must be on the
   * floor level) that is solid and not under the control of any player,
   * control will be granted on that dice.
   * <p>
   * In all other cases, the control request will be rejected.
   * <p>
   * On a successful request, this method sets all necessary model state to
   * represent the new situation.
   * 
   * @param p the player requesting control over a dice
   * @return the dice that is under the control of the player or 
   *         <code>None</code>
   */
  def requestControl(p: Player) : Option[Dice] = p.state match {
    case Player.Standing(t) => t.raised.content match {
      case Occupied(d) => requestControl(t.raised, p)
      case _ => requestControl(t.floor, p)
    }
    case Player.Controlling(d) => Some(d)
    case _ => None
  }
  
  /**
   * Helper method for requestControl(Player)
   */
  private def requestControl(where: Space, p: Player) = where.content match {
    case Occupied(d) => d.state match {
      case Dice.Solid(s, c) => 
        if (c.isEmpty) {
          d.state = Dice.Solid(s, Some(p))
          p.state = Player.Controlling(d)
          Some(d) 
        } else {
          None
        }
      case _ => None
    }
    case _ => None
  }
  
  /**
   * Requests for player <code>p</code> to move in direction <code>dir</code>.
   * <p>
   * If the player is not controlling a dice and the position after the move
   * would be still on the game board, the request is granted.
   * <p>
   * If the player is controlling a dice, the dice is "solid", the destination
   * is within board bounds and the destination tile has an empty space on top,
   * the request is granted.
   * <p>
   * If the player is already moving to the same position (with or without 
   * dice), the request is granted.
   * <p>
   * In all other cases, the movement request will be rejected.
   * <p>
   * On a successful request, this method sets all necessary model state to
   * represent the new situation.
   * 
   * @param p the player requesting to move
   * @param dir the direction the player wants to move in
   * @return whether the move was allowed (and started)
   */
  def requestMove(p: Player, dir: Direction.Value) : Boolean = p.state match {
    /* This is the easy case: the player controls no dice and just wants to move
     * around.
     */
    case Player.Standing(t @ Tile(x, y)) => {
      val pos = board.positionInDir(t, dir)
      
      if (board.isWithinBounds(pos)) {
        val mov = Player.Moving(p, t, board(pos), nowFor(MOVE_DURATION))
        p.state = mov
        timedStuff = mov :: timedStuff
        true
      } else false // destination out of board bounds
    }
    
    /* Somewhat tricky: the player controls a dice and wants to move along with
     * it. This is only granted if the target position is wihin bounds and the
     * tile at that position is free to be moved to.
     */
    case Player.Controlling(d) => d.state match {
      case Dice.Solid(s, Some(c)) if (p == c) => {
        val pos = board.positionInDir(s.tile, dir)
        
        if (board.isWithinBounds(pos)) {
          val t = board(pos)
          t.floor.content match {
            case Empty => {
              startDiceMovement(d, p, s, t.floor, dir)
              true // floor is free to be moved to
            }
            case _ : Movement => false // destination floor involved in movement
            case Occupied(fd) => fd.state match {
              
              // floor level occupied -> further analysis
              
              case Dice.Solid(_, c) => 
                if (c != None) false // floor dice controlled by another player
                else t.raised.content match {
                  case Empty => {
                    startDiceMovement(d, p, s, t.raised, dir)                
                    true // raised is free to be moved to
                  }
                  case _ => false // destination floor + raised non-empty
                }
              case _ => false // can only move onto solid dice
              
            } 
          }
        } else false // destination out of board bounds 
      }
      
      /* Player wants to move the dice she's already moving to the same place
       * it's already moving to: leave things as they are and return 
       * <code>true</code>.
       */
      case Dice.Moving(m @ Movement(_, a, b, _, _), c) 
        if (p == c && b.tile.pos == board.positionInDir(a.tile, dir)) => true
      
      case _ => false // dice appearing, bursting or moving in another direction
    }
    
    /* Player wants to move to the same place she's already moving to: 
     * leave things as they are and return <code>true</code>.
     */
    case Player.Moving(_, a, b, _) if (b.pos == board.positionInDir(a, dir)) 
      => true
    
    case _ => false
  }
  
  /**
   * Helper method for <code>requestMove</code> that updates the game model
   * state to actually represent the movement of a dice.
   * 
   * @param d the dice that moves
   * @param p the player controlling the dice
   * @param from the starting position
   * @param to the destination position
   * @param dir the movement direction
   */
  private def startDiceMovement(d: Dice, p: Player, from: Space, to: Space, 
                            dir: Direction.Value) : Unit = {
    val transform = if (from.isFloor == to.isFloor) dir match {
      case Direction.UP => Transform.ROTATE_UP
      case Direction.DOWN => Transform.ROTATE_DOWN
      case Direction.RIGHT => Transform.ROTATE_RIGHT
      case Direction.LEFT => Transform.ROTATE_LEFT
    } else dir match {
      case Direction.UP | Direction.DOWN => Transform.FLIP_UP_OR_DOWN
      case Direction.LEFT | Direction.RIGHT => Transform.FLIP_LEFT_OR_RIGHT
    }
      
    val m = Movement(d, from, to, nowFor(MOVE_DURATION), transform)
    
    from.content = m
    to.content = m
    d.state = Dice.Moving(m, p)
    timedStuff = m :: timedStuff
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
      val d = new Dice()
      board(x, y).floor.content = Occupied(d)
      val app = Dice.Appearing(board(x, y).floor, nowFor(APPEAR_DURATION))
      d.state = app
      timedStuff = app :: timedStuff
      Some(d)
    }
    case _ => None
  }
}
