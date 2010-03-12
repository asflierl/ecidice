package ecidice.model.dice

import ecidice.model._
import ecidice.util._

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
 * <p>
 * This class is final because it is not designed for extension through 
 * inheritance (esp. considering equals(Any)).
 * 
 * @author Andreas Flierl
 */
abstract class Dice [A <: Dice[_]] protected (
    val top: Int, 
    val right: Int, 
    val front: Int,
    private[dice] val serial: Long) 
{ this: A =>
  
  def bottom = opposite(top)
  def left = opposite(right)
  def back = opposite(front)
  
  private def opposite(eyes: Int) = 7 - eyes
  
  protected def create(newTop: Int, newRight: Int, newFront: Int): A
  
  def change(how: Transform.Value) = {
    how match {
      case Transform.ROTATE_BACKWARD => create(front, right, bottom)
      case Transform.ROTATE_FORWARD => create(back, right, top)
      case Transform.ROTATE_RIGHT => create(left, top, front)
      case Transform.ROTATE_LEFT => create(right, bottom, front)
      case Transform.SPIN_CLOCKWISE => create(top, back, right)
      case Transform.SPIN_COUNTERCLOCKWISE => create(top, front, left)
      case Transform.FLIP_UP_OR_DOWN => create(bottom, right, back)
      case Transform.FLIP_LEFT_OR_RIGHT => create(bottom, left, front)
    }
  }
  
  override def equals(obj: Any) = obj match {
    case other: Dice[_] => other.serial == serial
    case _ => false
  }
  
  override def hashCode = HashCode(serial)
  
  override def toString = "Dice[%d](%d-%d-%d)".format(serial, top, right, front)
}

object Dice {//TODO not thread-safe, might need a fix in the context of more threads
  private var serial = 0L

  private[dice] def nextSerial = {
    val before = serial
    serial += 1L
    before
  }
  
//  def appear()
}
