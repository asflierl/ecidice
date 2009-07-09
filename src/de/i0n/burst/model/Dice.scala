package de.i0n.burst.model

import scala.compat.Platform

/**
 * A single 6-side dice.
 * <p>
 * The dice layout is 
 * <pre>
 *  3
 *  6512
 *  4
 * </pre>
 * where 6 is on top, 4 in front, 5 on the right side. 
 * This can alternatively be represented as
 * <pre>
 *  65
 *  4
 * </pre>
 * to sufficiently describe the rotation of the dice.
 * 
 * @author Andreas Flierl
 */
class Dice(birthplace: Space) {
  private var topFace = 6
  private var frontFace = 4
  private var rightFace = 5
  
  /**
   * Holds the state of this dice in respect to the game rules.
   */
  val state = Dice.Appearing(birthplace, new Timespan(Dice.TIME_TO_APPEAR))
  
  def top = topFace
  def bottom = opposite(topFace)
  def front = frontFace
  def back = opposite(frontFace)
  def right = rightFace
  def left = opposite(rightFace)
  
  private def opposite(eyes: Int) = 7 - eyes
  
  private def set(t: Int, r: Int, f: Int) = {
    topFace = t
    rightFace = r
    frontFace = f
  }
  
  def rotateUp() = set(front, right, bottom)
  def rotateDown() = set(back, right, top)
  def rotateRight() = set(left, top, front)
  def rotateLeft() = set(right, bottom, front)
  
  def spinClockwise() = set(top, back, right)
  def spinCounterClockwise() = set(top, front, left)
  
  def flipUpOrDown() = set(bottom, right, back)
  def flipLeftOrRight() = set(bottom, left, front)
}
object Dice {
  val TIME_TO_APPEAR = 5000L
  
  /**
   * Supertype of a dice's possible states.
   */
  abstract class State
  
  /**
   * The dice is appearing. It can not be controlled nor moved. It occupies 
   * some space.
   */
  case class Appearing(where: Space, when: Timespan) extends State
  
  /**
   * The dice is solid now. It can be controlled by a player. It occupies
   * some space.
   */
  case class Solid(where: Space, controller: Option[Player]) extends State
  
  /**
   * The dice is moving. During movement, it is always controlled by a player.
   * The movement object defines the space occupied while moving.
   */
  case class Moving(move: Movement, controller: Player) extends State
  
  /**
   * The dice will burst soon-ish. A burst is always initiated by a player. 
   * The initiator can change as more dice are added to the burst. The dice
   * occupies some space.
   */
  case class Bursting(var initiator: Player, when: Timespan, where: Space) 
    extends State
}