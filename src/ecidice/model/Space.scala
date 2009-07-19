package ecidice.model

/**
 * Represents an area or space on the game board, either on the ground or on a
 * raised level. Such a space can be empty, occupied by a dice or be the start
 * or destination of a dice's movement.
 * 
 * @author Andreas Flierl
 * 
 * @param tile the tile that provides this space
 * @param content what's in this space
 */
class Space(val tile: Tile, var content: Content) {
  /** Returns whether this space is in the floor level. */
  def isFloor = (this == tile.floor)
  
  /** 
   * Returns whether this space is on the raised level (i.e. on top of a 
   * dice). 
   */
  def isRaised = (this == tile.raised)
}

abstract class Content

/**
 * Denotes that a space is empty. A dice can move or appear here (if this is
 * on the floor level).
 */
case object Empty extends Content

/**
 * This marks a space as occupied (by a dice). Other dice can not move or
 * appear here.
 */
case class Occupied(d: Dice) extends Content

/**
 * An instance of this class is present on the "from" and "to" spaces that are
 * involved in a dice's movement during the movement.
 */
case class Movement(from: Space, to: Space, when: Timespan) extends Content
