package de.i0n.ecidice.model

/**
 * Represents the game board, i.e. the floor tiles that the dice rest upon.
 * 
 * @author Andreas Flierl
 * 
 * @param width the number of tiles from left to right
 * @param depth the number of tiles from front to back
 */
class Board(val width: Int, val depth: Int) {
  val tiles = new Array[Array[Tile]](width, depth)
  for (x <- Stream.range(0, depth)) {
    for (y <- Stream.range(0, width)) {
      tiles(x)(y) = new Tile(x, y)
    }
  }
  
  def apply(x: Int, y: Int) = {
    tiles(x)(y)
  }
}
