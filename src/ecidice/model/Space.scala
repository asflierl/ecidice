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
