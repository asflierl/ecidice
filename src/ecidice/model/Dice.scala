package ecidice.model

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
 * where 6 is on top, 5 on the right side, 4 on the front. 
 * This can alternatively be represented as
 * <pre>
 *  65
 *  4
 * </pre>
 * to sufficiently describe the rotation of the dice.
 * 
 * @author Andreas Flierl
 */
class Dice private {
  private var birthplace : Space = _
  private var topFace = 6
  private var rightFace = 5
  private var frontFace = 4
  
  /**
   * Holds the state of this dice in respect to the game rules.
   */
  var state : Dice.State = _
  
  /** Returns the number of eyes on the top face. */
  def top = topFace
  
  /** Returns the number of eyes on the bottom face. */
  def bottom = opposite(topFace)
  
  /** Returns the number of eyes on the right face. */
  def right = rightFace
  
  /** Returns the number of eyes on the left face. */
  def left = opposite(rightFace)
  
  /** Returns the number of eyes on the front face. */
  def front = frontFace
  
  /** Returns the number of eyes on the back face. */
  def back = opposite(frontFace)
  
  /**
   * Returns the number on the opposite site of the specified face, i.e. 
   * <code>7 - eyes</code>.
   * 
   * @param eyes the eyes on the face whose opposite to return
   * @return the number of eyes on the opposite side
   */
  private def opposite(eyes: Int) = 7 - eyes
  
  /**
   * Assigns this dice's top, right and front faces. No consistency checking is
   * done by this method.
   * 
   * @param t the new top face
   * @param r the new right face
   * @param f the new front face
   */
  private def set(t: Int, r: Int, f: Int) {
    topFace = t
    rightFace = r
    frontFace = f
  }
  
  /** Rotates this dice upwards (to the back). */
  def rotateUp() = set(front, right, bottom)
  
  /** Rotates this dice downwards (to the front). */
  def rotateDown() = set(back, right, top)
  
  /** Rotates this dice to the right. */
  def rotateRight() = set(left, top, front)
  
  /** Rotates this dice to the left. */
  def rotateLeft() = set(right, bottom, front)
  
  /** Spins this dice clock-wise (top and bottom remain unchanged). */
  def spinClockwise() = set(top, back, right)
  
  /** Spins this dice counter-clock-wise (top and bottom remain unchanged). */
  def spinCounterClockwise() = set(top, front, left)
  
  /** Flips this dice 180&deg; up (or down, doesn't matter). */
  def flipUpOrDown() = set(bottom, right, back)
  
  /** Flips this dice 180&deg; left (or right, doesn't matter). */
  def flipLeftOrRight() = set(bottom, left, front)
}
object Dice {
  val TIME_TO_APPEAR = 5000L
  
  /**
   * Creates a new dice instance that appears in the specified space. The
   * space instance's content is set to "occupied by the new dice".
   * 
   * @param game the game this dice will participate in
   * @param birthplace the space this dice will occupy initially
   */
  def apply(game: Game, birthplace: Space) = {
    val d = new Dice()
    d.birthplace = birthplace
    birthplace.content = Occupied(d)
    d.state = Dice.Appearing(birthplace, new Timespan(game, game.now, 
                                                      TIME_TO_APPEAR))
    d
  }
  
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
