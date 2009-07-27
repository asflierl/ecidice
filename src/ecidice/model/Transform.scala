package ecidice.model

/**
 * Lists possible transformations of a dice.
 * 
 * @author Andreas Flierl
 */
object Transform extends Enumeration {
  /** Rotates a dice upwards (to the back). */
  val ROTATE_UP = Value
  
  /** Rotates a dice downwards (to the front). */
  val ROTATE_DOWN = Value
  
  /** Rotates a dice to the right. */
  val ROTATE_LEFT = Value
  
  /** Rotates a dice to the left. */
  val ROTATE_RIGHT = Value
  
  /** Spins a dice clock-wise (top and bottom remain unchanged). */
  val SPIN_CLOCKWISE = Value
  
  /** Spins a dice counter-clock-wise (top and bottom remain unchanged). */
  val SPIN_COUNTERCLOCKWISE = Value
  
  /** Flips a dice 180&deg; up (or down, doesn't matter). */
  val FLIP_UP_OR_DOWN = Value
  
  /** Flips a dice 180&deg; left (or right, doesn't matter). */
  val FLIP_LEFT_OR_RIGHT = Value
}
