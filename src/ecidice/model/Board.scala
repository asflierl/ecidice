package ecidice.model

/**
 * Represents the game board, i.e. the floor tiles that the dice rest upon.
 * 
 * @author Andreas Flierl
 * 
 * @param width the number of tiles from left to right
 * @param depth the number of tiles from front to back
 */
class Board(val width: Int, val depth: Int) {
  private val tiles = new Array[Array[Tile]](width, depth)
  for (x <- Stream.range(0, depth)) {
    for (y <- Stream.range(0, width)) {
      tiles(x)(y) = new Tile(x, y)
    }
  }
  
  val spawnPoints = tiles(0)(0) :: tiles(width - 1)(0) :: 
    tiles(width - 1)(depth - 1) :: tiles(0)(depth - 1) :: Nil
  
  /**
   * Returns the tile at the specified position.
   * 
   * @param x the horizontal position (left to right)
   * @param y the vertical position (front to back)
   * @return the tile at the specified coordinates
   */
  def apply(x: Int, y: Int) : Tile = {
    tiles(x)(y)
  }
  
  def apply(p: (Int, Int)) : Tile = apply(p._1, p._2)
  
  def isWithinBounds(x: Int, y: Int) : Boolean = 
    (x >= 0 && x < width) && (y >= 0 && y < depth)
  
  def isWithinBounds(p: (Int, Int)) : Boolean = isWithinBounds(p._1, p._2)
}
